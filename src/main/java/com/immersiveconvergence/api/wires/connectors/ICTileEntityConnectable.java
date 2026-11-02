package com.immersiveconvergence.api.wires.connectors;

import com.immersiveconvergence.api.block.ICTileEntityBase;
import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICInternalLink;
import com.immersiveconvergence.api.wires.ICTargetingInfo;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.ICWireCause;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.api.wires.ICWires;
import com.immersiveconvergence.api.wires.IICConnector;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("unused") public abstract class ICTileEntityConnectable extends ICTileEntityBase implements IICConnector {

    protected ICWireType limitType = null;

    protected boolean isRelay() { return false; }

    @Override @Nonnull public Collection<ICConnectionPoint> points() { return Collections.singleton(new ICConnectionPoint(getPos(), 0)); }

    @Override @Nullable public ICConnectionPoint pointAt(@Nonnull ICTargetingInfo target, @Nonnull Vec3i offset) { return new ICConnectionPoint(getPos(), 0); }

    @Override public boolean accepts(@Nonnull ICConnectionPoint point, @Nonnull ICWireType type) { return false; }

    @Override @Nonnull public BlockPos master() { return getPos(); }

    @Override @Nonnull public Collection<BlockPos> ignored() { return Collections.emptySet(); }

    @Override @Nonnull public Collection<ICInternalLink> links() { return Collections.emptySet(); }

    @Override public boolean passes(@Nonnull ICConnectionPoint point) { return true; }

    @Override public void onWireAdded(@Nonnull ICConnectionPoint point, @Nonnull ICWire wire) { limitType = wire.getType(); }

    @Override public void onWireRemoved(@Nonnull ICConnectionPoint point, @Nonnull ICWire wire, @Nonnull ICWireCause cause) { forget(wire.getType()); }

    public final boolean limitAllows(@Nonnull ICWireType type) { return limitType == null || (isRelay() && ICWireType.canMix(limitType, type)); }

    @Nonnull public ICConnectionPoint pointToward(@Nonnull BlockPos otherEnd, @Nonnull ICWireType type) { return new ICConnectionPoint(getPos(), 0); }

    @Nullable public ICWireType cableLimiter(@Nonnull ICTargetingInfo target) { return limitType; }

    @Nullable public Set<?> wireConnections() { return null; }

    protected final void forget(@Nonnull ICWireType type) {
        if (connectionCount() == 0 && type == limitType) { this.limitType = null; }
        wiresChanged();
    }

    protected final void wiresChanged() {
        markDirty();
        if (world != null) { markContainingBlockForUpdate(null); }
    }

    private int connectionCount() { return ICWires.connectionCount(world, pos); }

    public void syncConnectionPoints() { ICWires.syncConnectionPoints(this); }

    @Override public void onLoad() {
        super.onLoad();
        ICWires.connectableLoaded(this);
    }

    @Override public void invalidate() {
        super.invalidate();
        ICWires.connectableInvalidated(this);
    }

    @Override public void readCustomNBT(@Nonnull NBTTagCompound nbt, boolean descPacket) { limitType = ICWireType.readFromNBT(nbt, "limitType"); }

    @Override public void writeCustomNBT(@Nonnull NBTTagCompound nbt, boolean descPacket) {
        if (limitType != null) { limitType.writeToNBT(nbt, "limitType"); }
    }
}
