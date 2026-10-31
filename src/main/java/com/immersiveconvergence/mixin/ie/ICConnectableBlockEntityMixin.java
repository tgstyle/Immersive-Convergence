package com.immersiveconvergence.mixin.ie;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.block.BaseBlockEntity;
import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.api.wires.IICEnergyConnector;
import com.immersiveconvergence.api.wires.IICEnergySource;
import com.immersiveconvergence.api.wires.connectors.ICConnectableBlockEntity;
import com.immersiveconvergence.common.energy.wires.ICWireSavedData;
import com.immersiveconvergence.common.ie.IEWireBridge;
import com.immersiveconvergence.common.ie.IEWireEvents;

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
import org.spongepowered.asm.mixin.Unique;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

@Mixin(value = ICConnectableBlockEntity.class, remap = false) public abstract class ICConnectableBlockEntityMixin extends BaseBlockEntity implements IImmersiveConnectable, EnergyConnector {
    protected ICConnectableBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }

    @SuppressWarnings("ConstantConditions") @Unique private ICConnectableBlockEntity immersiveconvergence$self() { return (ICConnectableBlockEntity) (Object) this; }

    @Unique @Nullable private IICEnergyConnector immersiveconvergence$energy() { return (Object) this instanceof IICEnergyConnector energy ? energy : null; }

    @Unique @Nullable private IICEnergySource immersiveconvergence$source() { return (Object) this instanceof IICEnergySource source ? source : null; }

    @Unique @Nullable private ICConnectionPoint immersiveconvergence$sinkPoint() {
        IICEnergySource source = immersiveconvergence$source();
        for (ICConnectionPoint point : immersiveconvergence$self().points()) {
            if (source == null || source.isSink(point)) { return point; }
        }
        return null;
    }

    @Override public BlockPos getConnectionMaster(@Nullable WireType cableType, TargetingInfo target) { return immersiveconvergence$self().master(); }

    @Override public boolean canConnectCable(WireType cableType, ConnectionPoint target, Vec3i offset) { return ICMods.immersiveEngineering() && immersiveconvergence$self().accepts(IEWireBridge.point(target), IEWireBridge.of(cableType)); }

    @Override public void connectCable(WireType cableType, ConnectionPoint target, IImmersiveConnectable other, ConnectionPoint otherTarget) {
        ICConnectionPoint point = IEWireBridge.point(target);
        ICConnectionPoint otherPoint = IEWireBridge.point(otherTarget);
        immersiveconvergence$self().onWireAdded(point, new ICWire(point, Vec3.ZERO, otherPoint, Vec3.ZERO, IEWireBridge.of(cableType)));
        ICWireSavedData.ieWireAdded(level, point, otherPoint);
    }

    @Override @Nullable public ConnectionPoint getTargetedPoint(TargetingInfo info, Vec3i offset) { return IEWireBridge.toIE(immersiveconvergence$self().pointAt(IEWireBridge.targeting(info), offset)); }

    @Override public void removeCable(@Nullable Connection connection, ConnectionPoint attachedPoint) {
        ICConnectionPoint point = IEWireBridge.point(attachedPoint);
        if (connection == null) {
            ICWireSavedData.ieWireRemoved(level, point, null);
            return;
        }
        if (connection.isInternal()) { return; }
        ICConnectionPoint otherPoint = IEWireBridge.point(connection.getOtherEnd(attachedPoint));
        ICWireType type = IEWireBridge.of(connection.type);
        immersiveconvergence$self().onWireRemoved(point, new ICWire(point, Vec3.ZERO, otherPoint, Vec3.ZERO, type), IEWireEvents.cause());
        ICWireSavedData.ieWireRemoved(level, point, otherPoint);
    }

    @Override public Vec3 getConnectionOffset(ConnectionPoint here, ConnectionPoint other, WireType type) { return immersiveconvergence$self().offset(IEWireBridge.point(here), IEWireBridge.of(type)); }

    @Override public Collection<ConnectionPoint> getConnectionPoints() { return immersiveconvergence$self().points().stream().map(cp -> new ConnectionPoint(cp.position(), cp.index())).toList(); }

    @Override public Iterable<? extends Connection> getInternalConnections() { return immersiveconvergence$self().links().stream().map(link -> new Connection(link.getFirst().position(), link.getFirst().index(), link.getSecond().index())).toList(); }

    @Override public BlockPos getPosition() { return getBlockPos(); }

    @Override public Collection<ResourceLocation> getRequestedHandlers() { return immersiveconvergence$energy() != null ? List.of(EnergyTransferHandler.ID) : List.of(); }

    @Override public boolean isSource(ConnectionPoint cp) {
        IICEnergySource source = immersiveconvergence$source();
        return source != null && source.isSource(IEWireBridge.point(cp));
    }

    @Override public boolean isSink(ConnectionPoint cp) {
        IICEnergySource source = immersiveconvergence$source();
        return immersiveconvergence$energy() != null && (source == null || source.isSink(IEWireBridge.point(cp)));
    }

    @Override public int getAvailableEnergy() {
        IICEnergySource source = immersiveconvergence$source();
        return source != null ? source.available() : 0;
    }

    @Override public int getRequestedEnergy() {
        IICEnergyConnector energy = immersiveconvergence$energy();
        ICConnectionPoint point = immersiveconvergence$sinkPoint();
        return energy != null && point != null ? energy.demand(point) : 0;
    }

    @Override public void insertEnergy(int amount) {
        IICEnergyConnector energy = immersiveconvergence$energy();
        ICConnectionPoint point = immersiveconvergence$sinkPoint();
        if (energy != null && point != null) { energy.accept(point, amount, false); }
    }

    @Override public void extractEnergy(int amount) {
        IICEnergySource source = immersiveconvergence$source();
        if (source != null) { source.extracted(amount); }
    }

    @Override public void onEnergyPassedThrough(double amount) {
        IICEnergyConnector energy = immersiveconvergence$energy();
        ICConnectionPoint point = immersiveconvergence$sinkPoint();
        if (energy != null && point != null) { energy.onPassthrough(point, (int) amount); }
    }

    @Unique private boolean immersiveconvergence$joinsIE() { return ICMods.immersiveEngineering() && immersiveconvergence$self().canConnect(); }

    @Override public void onLoad() {
        super.onLoad();
        if (level != null && immersiveconvergence$joinsIE()) { ConnectorBlockEntityHelper.onChunkLoad(this, level); }
    }

    @Override public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if (level != null && immersiveconvergence$joinsIE()) { ConnectorBlockEntityHelper.onChunkUnload(GlobalWireNetwork.getNetwork(level), this); }
    }

    @Override public void setRemovedIE() {
        super.setRemovedIE();
        if (level != null && immersiveconvergence$joinsIE()) { ConnectorBlockEntityHelper.remove(level, this); }
    }
}
