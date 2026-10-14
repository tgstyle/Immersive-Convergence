package com.immersiveconvergence.mixin.ie;

import com.immersiveconvergence.api.block.BaseBlockEntity;
import com.immersiveconvergence.api.energy.ICConnectableBlockEntity;
import com.immersiveconvergence.api.energy.ICConnectionPoint;
import com.immersiveconvergence.api.energy.ICTargetingInfo;
import com.immersiveconvergence.api.energy.ICWireType;
import com.immersiveconvergence.api.energy.IICEnergyConnector;
import com.immersiveconvergence.common.ie.IEWireBridge;

import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.wires.Connection;
import blusunrize.immersiveengineering.api.wires.ConnectionPoint;
import blusunrize.immersiveengineering.api.wires.ConnectorBlockEntityHelper;
import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.wires.WireType;
import blusunrize.immersiveengineering.api.wires.localhandlers.EnergyTransferHandler;
import blusunrize.immersiveengineering.api.wires.localhandlers.EnergyTransferHandler.EnergyConnector;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

@Mixin(value = ICConnectableBlockEntity.class, remap = false)
public abstract class ICConnectableBlockEntityMixin extends BaseBlockEntity implements IImmersiveConnectable, EnergyConnector {
    protected ICConnectableBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }

    @Shadow(remap = false) public abstract boolean canConnect();
    @Shadow(remap = false) @Nonnull public abstract BlockPos connectionMaster(@Nullable ICWireType cable, @Nonnull ICTargetingInfo target);
    @Shadow(remap = false) public abstract boolean acceptsCable(@Nonnull ICWireType cable, @Nonnull ICConnectionPoint target, @Nonnull Vec3i offset);
    @Shadow(remap = false) public abstract void cableConnected(@Nonnull ICWireType cable, @Nonnull ICConnectionPoint target);
    @Shadow(remap = false) @Nullable public abstract ICConnectionPoint targetedPoint(@Nonnull ICTargetingInfo target, @Nonnull Vec3i offset);
    @Shadow(remap = false) public abstract void cableRemoved(@Nullable ICWireType cable, @Nonnull ICConnectionPoint attachedPoint);
    @Shadow(remap = false) @Nonnull public abstract Vec3 connectionOffset(@Nonnull ICConnectionPoint here, @Nonnull ICConnectionPoint other, @Nonnull ICWireType cable);
    @Shadow(remap = false) @Nonnull public abstract Collection<ICConnectionPoint> connectionPoints();

    @Unique @Nullable private IICEnergyConnector immersiveconvergence$energy() { return (Object) this instanceof IICEnergyConnector energy ? energy : null; }

    @Override public BlockPos getConnectionMaster(@Nullable WireType cableType, TargetingInfo target) { return connectionMaster(IEWireBridge.ofNullable(cableType), IEWireBridge.targeting(target)); }

    @Override public boolean canConnectCable(WireType cableType, ConnectionPoint target, Vec3i offset) { return acceptsCable(IEWireBridge.of(cableType), IEWireBridge.point(target), offset); }

    @Override public void connectCable(WireType cableType, ConnectionPoint target, IImmersiveConnectable other, ConnectionPoint otherTarget) { cableConnected(IEWireBridge.of(cableType), IEWireBridge.point(target)); }

    @Override @Nullable public ConnectionPoint getTargetedPoint(TargetingInfo info, Vec3i offset) { return IEWireBridge.toIE(targetedPoint(IEWireBridge.targeting(info), offset)); }

    @Override public void removeCable(@Nullable Connection connection, ConnectionPoint attachedPoint) { cableRemoved(connection == null ? null : IEWireBridge.of(connection.type), IEWireBridge.point(attachedPoint)); }

    @Override public Vec3 getConnectionOffset(ConnectionPoint here, ConnectionPoint other, WireType type) { return connectionOffset(IEWireBridge.point(here), IEWireBridge.point(other), IEWireBridge.of(type)); }

    @Override public Collection<ConnectionPoint> getConnectionPoints() { return connectionPoints().stream().map(cp -> new ConnectionPoint(cp.position(), cp.index())).toList(); }

    @Override public BlockPos getPosition() { return getBlockPos(); }

    @Override public Collection<ResourceLocation> getRequestedHandlers() { return immersiveconvergence$energy() != null ? List.of(EnergyTransferHandler.ID) : List.of(); }

    @Override public boolean isSource(ConnectionPoint cp) {
        IICEnergyConnector energy = immersiveconvergence$energy();
        return energy != null && energy.isEnergySource(IEWireBridge.point(cp));
    }

    @Override public boolean isSink(ConnectionPoint cp) {
        IICEnergyConnector energy = immersiveconvergence$energy();
        return energy != null && energy.isEnergySink(IEWireBridge.point(cp));
    }

    @Override public int getAvailableEnergy() {
        IICEnergyConnector energy = immersiveconvergence$energy();
        return energy != null ? energy.energyAvailable() : 0;
    }

    @Override public int getRequestedEnergy() {
        IICEnergyConnector energy = immersiveconvergence$energy();
        return energy != null ? energy.energyRequested() : 0;
    }

    @Override public void insertEnergy(int amount) {
        IICEnergyConnector energy = immersiveconvergence$energy();
        if (energy != null) { energy.energyInserted(amount); }
    }

    @Override public void extractEnergy(int amount) {
        IICEnergyConnector energy = immersiveconvergence$energy();
        if (energy != null) { energy.energyExtracted(amount); }
    }

    @Override public void onEnergyPassedThrough(double amount) {
        IICEnergyConnector energy = immersiveconvergence$energy();
        if (energy != null) { energy.energyPassedThrough(amount); }
    }

    @Override public void onLoad() {
        super.onLoad();
        if (level != null && canConnect()) { ConnectorBlockEntityHelper.onChunkLoad(this, level); }
    }

    @Override public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if (level != null && canConnect()) { ConnectorBlockEntityHelper.onChunkUnload(GlobalWireNetwork.getNetwork(level), this); }
    }

    @Override public void setRemovedIE() {
        super.setRemovedIE();
        if (level != null && canConnect()) { ConnectorBlockEntityHelper.remove(level, this); }
    }
}
