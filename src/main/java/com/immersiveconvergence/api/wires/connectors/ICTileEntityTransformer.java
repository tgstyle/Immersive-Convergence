package com.immersiveconvergence.api.wires.connectors;

import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICInternalLink;
import com.immersiveconvergence.api.wires.ICTargetingInfo;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.ICWireCause;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.api.wires.ICWires;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public abstract class ICTileEntityTransformer extends ICTileEntityConnector {
    private static final String TOP = "top";
    private static final String MIDDLE = "middle";
    private static final String[] CABLE = {"lowCable", "highCable"};
    private static final String[] END = {"lowEnd", "highEnd"};
    private static final String MORE = "morePorts";
    private static final String MORE_CABLE = "cable";
    private static final String MORE_END = "end";
    private static final double SPREAD = 0.3125;
    private final ICConnectorTier[] tiers;
    private final ICWireType[] cables;
    private final BlockPos[] ends;
    private boolean top;
    private boolean middle;

    protected ICTileEntityTransformer(ICConnectorTier first, ICConnectorTier second, ICConnectorTier... more) {
        super(first, true);
        tiers = new ICConnectorTier[2 + more.length];
        tiers[0] = first;
        tiers[1] = second;
        System.arraycopy(more, 0, tiers, 2, more.length);
        cables = new ICWireType[tiers.length];
        ends = new BlockPos[tiers.length];
    }

    public final boolean isTop() { return top; }

    public final boolean isUpperCell() { return top || middle; }

    public final void fill(EnumFacing facing) {
        middle = true;
        setFacing(facing);
        markDirty();
        markContainingBlockForUpdate(null);
    }

    public final void raise(EnumFacing facing) {
        top = true;
        setFacing(facing);
        markDirty();
        syncConnectionPoints();
        markContainingBlockForUpdate(null);
    }

    public final int portCount() { return tiers.length; }

    @Nonnull public final ICConnectorTier portTier(int port) { return tiers[port]; }

    @Nullable public final ICWireType portCable(int port) {
        if (savedState()) { return cables[port]; }
        ICWire wire = portWire(port);
        return wire == null ? null : wire.getType();
    }

    @Nullable public final BlockPos portEnd(int port) {
        if (savedState()) { return ends[port]; }
        ICWire wire = portWire(port);
        return wire == null ? null : wire.other(point(port)).getPos();
    }

    private boolean savedState() { return world == null || world.isRemote; }

    @Nullable private ICWire portWire(int port) {
        Collection<ICWire> wires = ICWires.network(world).wiresAt(point(port));
        return wires.isEmpty() ? null : wires.iterator().next();
    }

    private ICConnectionPoint point(int port) { return new ICConnectionPoint(getPos(), port); }

    @Override @Nonnull public Collection<ICConnectionPoint> points() {
        if (!top) { return Collections.emptyList(); }
        List<ICConnectionPoint> points = new ArrayList<>(tiers.length);
        for (int port = 0; port < tiers.length; port++) { points.add(point(port)); }
        return points;
    }

    @Override @Nullable public ICConnectionPoint pointAt(@Nonnull ICTargetingInfo target, @Nonnull Vec3i offset) { return top ? point(index(target)) : null; }

    @Override public boolean accepts(@Nonnull ICConnectionPoint point, @Nonnull ICWireType type) {
        int port = point.getIndex();
        return top && port >= 0 && port < tiers.length && carries(port, type) && portCable(port) == null;
    }

    @Override @Nonnull public Collection<ICInternalLink> links() {
        if (!top) { return Collections.emptySet(); }
        List<ICInternalLink> links = new ArrayList<>(tiers.length - 1);
        for (int port = 1; port < tiers.length; port++) { links.add(new ICInternalLink(point(port - 1), point(port), 0)); }
        return links;
    }

    @Override public void onWireAdded(@Nonnull ICConnectionPoint point, @Nonnull ICWire wire) {
        BlockPos end = wire.other(point).getPos();
        int port = portFor(point, end, wire.getType());
        if (port < 0) { return; }
        cables[port] = wire.getType();
        ends[port] = end;
        markDirty();
    }

    @Override public void onWireRemoved(@Nonnull ICConnectionPoint point, @Nonnull ICWire wire, @Nonnull ICWireCause cause) {
        int named = point.getIndex();
        int port = named >= 0 && named < tiers.length && wire.other(point).getPos().equals(ends[named]) ? named : portHolding(wire.other(point).getPos(), wire.getType());
        if (port >= 0) {
            cables[port] = null;
            ends[port] = null;
        }
        wiresChanged();
    }

    @Override @Nonnull public ICConnectionPoint pointToward(@Nonnull BlockPos otherEnd, @Nonnull ICWireType type) {
        int port = portHolding(otherEnd, type);
        return point(port >= 0 ? port : Math.max(0, port(type)));
    }

    @Override @Nullable public ICWireType cableLimiter(@Nonnull ICTargetingInfo target) { return portCable(index(target)); }

    @Override @Nonnull public Vec3d offset(@Nonnull ICConnectionPoint point, @Nonnull ICWireType type) {
        int port = point.getIndex();
        EnumFacing right = right();
        double side = SPREAD - 2 * SPREAD * port / (tiers.length - 1);
        double height = 0.5 + tiers[port].inset(false) - type.getRenderDiameter() / 2;
        return new Vec3d(0.5 + right.getXOffset() * side, height, 0.5 + right.getZOffset() * side);
    }

    private int index(ICTargetingInfo target) {
        EnumFacing right = right();
        double along = (target.hitX - 0.5) * right.getXOffset() + (target.hitZ - 0.5) * right.getZOffset();
        return MathHelper.clamp((int)Math.ceil((0.5 - along) * tiers.length) - 1, 0, tiers.length - 1);
    }

    @Override public float[] getBlockBounds() {
        if (!top) { return new float[] {0, 0, 0, 1, 1, 1}; }
        float height = 0.5F + (float)highestInset();
        if (right().getAxis() == EnumFacing.Axis.X) { return new float[] {0, 0, WIDTH_MIN, 1, height, WIDTH_MAX}; }
        return new float[] {WIDTH_MIN, 0, 0, WIDTH_MAX, height, 1};
    }

    private double highestInset() {
        double highest = 0;
        for (ICConnectorTier tier : tiers) { highest = Math.max(highest, tier.inset(false)); }
        return highest;
    }

    @Override public int getFacingLimitation() { return 6; }

    @Override public boolean mirrorFacingOnPlacement(EntityLivingBase placer) { return false; }

    private EnumFacing right() {
        EnumFacing facing = getFacing();
        return facing.getAxis() == EnumFacing.Axis.Y ? EnumFacing.EAST : facing.rotateY();
    }

    private boolean carries(int port, ICWireType cable) { return tiers[port].getCategory() == cable.getCategory(); }

    private int port(ICWireType cable) {
        for (int port = 0; port < tiers.length; port++) {
            if (carries(port, cable)) { return port; }
        }
        return -1;
    }

    private int portHolding(BlockPos end, ICWireType cable) {
        for (int port = 0; port < tiers.length; port++) {
            if (carries(port, cable) && end.equals(ends[port])) { return port; }
        }
        return -1;
    }

    private int portFor(ICConnectionPoint point, BlockPos end, ICWireType cable) {
        int clicked = point.getIndex();
        if (clicked >= 0 && clicked < tiers.length && carries(clicked, cable) && (cables[clicked] == null || end.equals(ends[clicked]))) { return clicked; }
        int held = portHolding(end, cable);
        if (held >= 0) { return held; }
        for (int port = 0; port < tiers.length; port++) {
            if (carries(port, cable) && cables[port] == null) { return port; }
        }
        return port(cable);
    }

    @Override public void readCustomNBT(@Nonnull NBTTagCompound nbt, boolean descPacket) {
        super.readCustomNBT(nbt, descPacket);
        top = nbt.getBoolean(TOP);
        middle = nbt.getBoolean(MIDDLE);
        for (int port = 0; port < CABLE.length; port++) {
            cables[port] = ICWireType.readFromNBT(nbt, CABLE[port]);
            ends[port] = nbt.hasKey(END[port]) ? BlockPos.fromLong(nbt.getLong(END[port])) : null;
        }
        NBTTagList more = nbt.getTagList(MORE, Constants.NBT.TAG_COMPOUND);
        for (int port = CABLE.length; port < cables.length; port++) {
            NBTTagCompound tag = more.getCompoundTagAt(port - CABLE.length);
            cables[port] = ICWireType.readFromNBT(tag, MORE_CABLE);
            ends[port] = tag.hasKey(MORE_END) ? BlockPos.fromLong(tag.getLong(MORE_END)) : null;
        }
    }

    @Override public void writeCustomNBT(@Nonnull NBTTagCompound nbt, boolean descPacket) {
        super.writeCustomNBT(nbt, descPacket);
        nbt.setBoolean(TOP, top);
        nbt.setBoolean(MIDDLE, middle);
        for (int port = 0; port < CABLE.length; port++) {
            ICWireType cable = cables[port];
            BlockPos end = ends[port];
            if (cable != null) { cable.writeToNBT(nbt, CABLE[port]); }
            if (end != null) { nbt.setLong(END[port], end.toLong()); }
        }
        if (tiers.length == CABLE.length) { return; }
        NBTTagList more = new NBTTagList();
        for (int port = CABLE.length; port < cables.length; port++) {
            NBTTagCompound tag = new NBTTagCompound();
            if (cables[port] != null) { cables[port].writeToNBT(tag, MORE_CABLE); }
            if (ends[port] != null) { tag.setLong(MORE_END, ends[port].toLong()); }
            more.appendTag(tag);
        }
        nbt.setTag(MORE, more);
    }
}
