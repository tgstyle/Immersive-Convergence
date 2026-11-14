package com.immersiveconvergence.api.wires.fittings;

import com.immersiveconvergence.api.block.FacingLimitation;
import com.immersiveconvergence.api.block.ModProperties;
import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICInternalLink;
import com.immersiveconvergence.api.wires.ICTargetingInfo;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.ICWireCause;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.api.wires.ICWires;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused") public abstract class ICTransformerBlockEntity extends ICFittingBlockEntity {
    private static final String CELL = "cell";
    private static final String[] CABLE = {"lowCable", "highCable"};
    private static final String[] END = {"lowEnd", "highEnd"};
    private static final String MORE = "morePorts";
    private static final String MORE_CABLE = "cable";
    private static final String MORE_END = "end";
    private final ICTransformerLayout layout;
    private final ICFittingTier[] tiers;
    private final ICWireType[] cables;
    private final BlockPos[] ends;
    private int cell;

    protected ICTransformerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, ICTransformerLayout layout, ICFittingTier first, ICFittingTier second, ICFittingTier... more) {
        super(type, pos, state, first, true);
        tiers = new ICFittingTier[2 + more.length];
        tiers[0] = first;
        tiers[1] = second;
        System.arraycopy(more, 0, tiers, 2, more.length);
        if (layout.terminalCount() != tiers.length) { throw new IllegalArgumentException("A transformer needs one terminal per port: " + layout.terminalCount() + " terminals, " + tiers.length + " ports"); }
        this.layout = layout;
        cables = new ICWireType[tiers.length];
        ends = new BlockPos[tiers.length];
    }

    public final boolean isTop() { return cell == layout.cells() - 1; }

    public final boolean isUpperCell() { return cell > 0; }

    public final int cell() { return cell; }

    @Override public BlockPos getModelOffset(BlockState state, Vec3i size) { return new BlockPos(0, cell, 0); }

    public final void assemble(@Nonnull Direction facing, int index) {
        cell = index;
        setFacing(facing);
        setChanged();
        if (isTop()) {
            syncConnectionPoints();
            wiresChanged();
        }
    }

    public final int portCount() { return tiers.length; }

    @Nonnull public final ICFittingTier portTier(int port) { return tiers[port]; }

    @Nullable public final ICWireType portCable(int port) {
        if (savedState()) { return cables[port]; }
        ICWire wire = portWire(port);
        return wire == null ? null : wire.getType();
    }

    @Nullable public final BlockPos portEnd(int port) {
        if (savedState()) { return ends[port]; }
        ICWire wire = portWire(port);
        return wire == null ? null : wire.other(point(port)).position();
    }

    private boolean savedState() { return level == null || level.isClientSide; }

    @Nullable private ICWire portWire(int port) {
        if (level == null) { return null; }
        Collection<ICWire> wires = ICWires.network(level).wiresAt(point(port));
        return wires.isEmpty() ? null : wires.iterator().next();
    }

    private ICConnectionPoint point(int port) { return new ICConnectionPoint(worldPosition, port); }

    private boolean isPort(int port) { return port >= 0 && port < tiers.length; }

    @Override @Nonnull public Collection<ICConnectionPoint> points() {
        if (!isTop()) { return List.of(); }
        List<ICConnectionPoint> points = new ArrayList<>(tiers.length);
        for (int port = 0; port < tiers.length; port++) { points.add(point(port)); }
        return points;
    }

    @Override @Nullable public ICConnectionPoint pointAt(@Nonnull ICTargetingInfo target, @Nonnull Vec3i offset) { return isTop() ? point(index(target)) : null; }

    @Override public boolean accepts(@Nonnull ICConnectionPoint point, @Nonnull ICWireType type) {
        int port = point.index();
        return isTop() && isPort(port) && tiers[port].getCategory() == type.getCategory() && portCable(port) == null;
    }

    @Override @Nonnull public Collection<ICInternalLink> links() {
        if (!isTop()) { return Set.of(); }
        List<ICInternalLink> links = new ArrayList<>(tiers.length - 1);
        for (int port = 1; port < tiers.length; port++) { links.add(new ICInternalLink(point(port - 1), point(port), 0)); }
        return links;
    }

    @Override public void onWireAdded(@Nonnull ICConnectionPoint point, @Nonnull ICWire wire) {
        int port = point.index();
        if (!isPort(port)) { return; }
        cables[port] = wire.getType();
        ends[port] = wire.other(point).position();
        wiresChanged();
    }

    @Override public void onWireRemoved(@Nonnull ICConnectionPoint point, @Nonnull ICWire wire, @Nonnull ICWireCause cause) {
        int port = point.index();
        if (isPort(port)) {
            cables[port] = null;
            ends[port] = null;
        }
        wiresChanged();
    }

    @Override public void wiresLoaded() {
        for (int port = 0; port < tiers.length; port++) {
            cables[port] = portCable(port);
            ends[port] = portEnd(port);
        }
    }

    @Override @Nonnull public Vec3 offset(@Nonnull ICConnectionPoint point, @Nonnull ICWireType type) {
        int port = point.index();
        Direction right = right();
        double along = layout.along(port);
        double height = layout.height(port) - cell - type.getRenderDiameter() / 2;
        return new Vec3(0.5 + right.getStepX() * along, height, 0.5 + right.getStepZ() * along);
    }

    private int index(ICTargetingInfo target) {
        Direction right = right();
        double along = (target.hitX() - 0.5) * right.getStepX() + (target.hitZ() - 0.5) * right.getStepZ();
        return Mth.clamp((int)Math.ceil((0.5 - along) * tiers.length) - 1, 0, tiers.length - 1);
    }

    @Override @Nonnull public VoxelShape getBlockBounds(@Nullable CollisionContext context) {
        if (right().getAxis() == Direction.Axis.X) { return Shapes.box(0, 0, layout.depthMin(), 1, 1, layout.depthMax()); }
        return Shapes.box(layout.depthMin(), 0, 0, layout.depthMax(), 1, 1);
    }

    @Override public FacingLimitation getFacingLimitation() { return FacingLimitation.HORIZONTAL_PREFER_SIDE; }

    @Override public boolean mirrorFacingOnPlacement() { return false; }

    @Override protected BlockState modelState(BlockState state) {
        BlockState faced = super.modelState(state);
        return faced.hasProperty(ModProperties.MULTIBLOCKSLAVE) ? faced.setValue(ModProperties.MULTIBLOCKSLAVE, isUpperCell()) : faced;
    }

    private Direction right() {
        Direction facing = getFacing();
        return facing.getAxis() == Direction.Axis.Y ? Direction.EAST : facing.getClockWise();
    }

    @Override public void readCustomNBT(@Nonnull CompoundTag nbt, boolean descPacket) {
        super.readCustomNBT(nbt, descPacket);
        cell = nbt.getInt(CELL);
        for (int port = 0; port < CABLE.length; port++) {
            cables[port] = ICWireType.readFromNBT(nbt, CABLE[port]);
            ends[port] = nbt.contains(END[port]) ? BlockPos.of(nbt.getLong(END[port])) : null;
        }
        ListTag more = nbt.getList(MORE, Tag.TAG_COMPOUND);
        for (int port = CABLE.length; port < cables.length; port++) {
            CompoundTag tag = more.getCompound(port - CABLE.length);
            cables[port] = ICWireType.readFromNBT(tag, MORE_CABLE);
            ends[port] = tag.contains(MORE_END) ? BlockPos.of(tag.getLong(MORE_END)) : null;
        }
    }

    @Override public void writeCustomNBT(@Nonnull CompoundTag nbt, boolean descPacket) {
        super.writeCustomNBT(nbt, descPacket);
        nbt.putInt(CELL, cell);
        for (int port = 0; port < CABLE.length; port++) {
            if (cables[port] != null) { cables[port].writeToNBT(nbt, CABLE[port]); }
            if (ends[port] != null) { nbt.putLong(END[port], ends[port].asLong()); }
        }
        if (tiers.length == CABLE.length) { return; }
        ListTag more = new ListTag();
        for (int port = CABLE.length; port < cables.length; port++) {
            CompoundTag tag = new CompoundTag();
            if (cables[port] != null) { cables[port].writeToNBT(tag, MORE_CABLE); }
            if (ends[port] != null) { tag.putLong(MORE_END, ends[port].asLong()); }
            more.add(tag);
        }
        nbt.put(MORE, more);
    }
}
