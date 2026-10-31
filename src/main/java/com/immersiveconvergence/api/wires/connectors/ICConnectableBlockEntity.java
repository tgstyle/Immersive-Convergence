package com.immersiveconvergence.api.wires.connectors;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.block.BaseBlockEntity;
import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICInternalLink;
import com.immersiveconvergence.api.wires.ICTargetingInfo;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.ICWireCause;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.api.wires.ICWires;
import com.immersiveconvergence.api.wires.IICConnector;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused") public abstract class ICConnectableBlockEntity extends BaseBlockEntity implements IICConnector {
    protected ICWireType limitType = null;

    public ICConnectableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }

    protected boolean isRelay() { return false; }

    public boolean canConnect() { return false; }

    @Override @Nonnull public Collection<ICConnectionPoint> points() { return List.of(new ICConnectionPoint(worldPosition, 0)); }

    @Override @Nullable public ICConnectionPoint pointAt(@Nonnull ICTargetingInfo target, @Nonnull Vec3i offset) { return new ICConnectionPoint(worldPosition, 0); }

    @Override public boolean accepts(@Nonnull ICConnectionPoint point, @Nonnull ICWireType type) { return false; }

    @Override @Nonnull public BlockPos master() { return worldPosition; }

    @Override @Nonnull public Collection<BlockPos> ignored() { return Set.of(); }

    @Override @Nonnull public Collection<ICInternalLink> links() { return Set.of(); }

    @Override public boolean passes(@Nonnull ICConnectionPoint point) { return true; }

    @Override public void onWireAdded(@Nonnull ICConnectionPoint point, @Nonnull ICWire wire) {
        limitType = wire.getType();
        setChanged();
    }

    @Override public void onWireRemoved(@Nonnull ICConnectionPoint point, @Nonnull ICWire wire, @Nonnull ICWireCause cause) { forget(wire.getType()); }

    public final boolean limitAllows(@Nonnull ICWireType type) { return limitType == null || (isRelay() && ICWireType.canMix(limitType, type)); }

    protected final void forget(@Nonnull ICWireType type) {
        if (unwired() && type == limitType) { this.limitType = null; }
        wiresChanged();
    }

    protected final void wiresChanged() {
        setChanged();
        if (level != null) { markContainingBlockForUpdate(null); }
    }

    private boolean unwired() {
        for (ICConnectionPoint point : points()) {
            if (!hasNoExternalConnections(point)) { return false; }
        }
        return true;
    }

    protected boolean ieWired() { return level != null && ICMods.immersiveEngineering(); }

    protected boolean hasNoExternalConnections(@Nonnull ICConnectionPoint cp) { return ICWires.hasNoExternalConnections(level, cp); }

    public void syncConnectionPoints() { ICWires.syncConnectionPoints(this); }

    @Override public void onLoad() {
        super.onLoad();
        ICWires.connectableLoaded(this);
    }

    @Override public void setRemovedIE() {
        super.setRemovedIE();
        ICWires.connectableInvalidated(this);
    }

    @Override public void readCustomNBT(@Nonnull CompoundTag nbt, boolean descPacket) { limitType = ICWireType.readFromNBT(nbt, "limitType"); }

    @Override public void writeCustomNBT(@Nonnull CompoundTag nbt, boolean descPacket) { if (limitType != null) { limitType.writeToNBT(nbt, "limitType"); } }
}
