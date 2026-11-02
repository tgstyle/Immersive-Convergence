package com.immersiveconvergence.mixin.integration.immersiveengineering.api.wires.connectors;

import com.immersiveconvergence.ImmersiveConvergence;
import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.api.wires.IICEnergyConnector;
import com.immersiveconvergence.api.wires.connectors.ICTileEntityConnectable;
import com.immersiveconvergence.common.energy.ICIEWireType;
import com.immersiveconvergence.common.energy.IEWireBridge;
import com.immersiveconvergence.common.energy.IEWireEvents;
import com.immersiveconvergence.common.energy.IEWireGraph;
import com.immersiveconvergence.common.energy.wires.ICWireSavedData;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.energy.wires.IICProxy;
import blusunrize.immersiveengineering.api.energy.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import blusunrize.immersiveengineering.api.energy.wires.WireType;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@Mixin(value = ICTileEntityConnectable.class, remap = false) public abstract class MixinICTileEntityConnectable extends net.minecraft.tileentity.TileEntity implements IImmersiveConnectable {

    @Unique private final List<Pair<Float, Consumer<Float>>> immersiveconvergence$sources = new ArrayList<>();
    @Unique private long immersiveconvergence$lastSourceUpdate = 0;

    @SuppressWarnings("ConstantConditions") @Unique private ICTileEntityConnectable immersiveconvergence$self() { return (ICTileEntityConnectable)(Object)this; }

    @Unique private BlockPos immersiveconvergence$otherEnd(Connection con) { return con.start.equals(pos) ? con.end : con.start; }

    @Unique private ICConnectionPoint immersiveconvergence$pointOf(Connection con) { return immersiveconvergence$self().pointToward(immersiveconvergence$otherEnd(con), IEWireBridge.required(con.cableType)); }

    @Unique private ICWire immersiveconvergence$wire(ICConnectionPoint point, BlockPos otherEnd, ICWireType type) { return new ICWire(point, Vec3d.ZERO, new ICConnectionPoint(otherEnd, 0), Vec3d.ZERO, type); }

    @Unique @Nullable private IICEnergyConnector immersiveconvergence$energy() {
        Object self = this;
        return self instanceof IICEnergyConnector ? (IICEnergyConnector)self : null;
    }

    @Override public boolean canConnect() { return !immersiveconvergence$self().points().isEmpty(); }

    @Override public boolean isEnergyOutput() { return immersiveconvergence$energy() != null; }

    @Override public int outputEnergy(int amount, boolean simulate, int energyType) {
        IICEnergyConnector energy = immersiveconvergence$energy();
        return energy == null ? 0 : energy.accept(new ICConnectionPoint(pos, 0), amount, simulate);
    }

    @Override public void onEnergyPassthrough(int amount) {
        IICEnergyConnector energy = immersiveconvergence$energy();
        if (energy != null) { energy.onPassthrough(new ICConnectionPoint(pos, 0), amount); }
    }

    @Override public BlockPos getConnectionMaster(@Nullable WireType cableType, TargetingInfo target) { return immersiveconvergence$self().master(); }

    @Override public boolean canConnectCable(WireType cableType, TargetingInfo target, @Nonnull Vec3i offset) {
        if (!ICMods.immersiveEngineering()) { return false; }
        ICConnectionPoint point = immersiveconvergence$self().pointAt(IEWireBridge.targeting(target), offset);
        return point != null && immersiveconvergence$self().accepts(point, IEWireBridge.required(cableType));
    }

    @Override public void connectCable(WireType cableType, TargetingInfo target, IImmersiveConnectable other) {
        ICWireType cable = IEWireBridge.required(cableType);
        BlockPos otherMaster = other.getConnectionMaster(cableType, target);
        ICConnectionPoint targeted = immersiveconvergence$self().pointAt(IEWireBridge.targeting(target), Vec3i.NULL_VECTOR);
        ICConnectionPoint point = targeted != null ? targeted : immersiveconvergence$self().pointToward(otherMaster, cable);
        immersiveconvergence$self().onWireAdded(point, immersiveconvergence$wire(point, otherMaster, cable));
        ICWireSavedData.ieWireAdded(world, pos, otherMaster, cable);
        IEWireGraph.invalidate(world);
    }

    @Override public WireType getCableLimiter(@Nonnull TargetingInfo target) { return IEWireBridge.toIE(immersiveconvergence$self().cableLimiter(IEWireBridge.targeting(target))); }

    @Override public boolean allowEnergyToPass(Connection con) { return immersiveconvergence$self().passes(con == null ? new ICConnectionPoint(pos, 0) : immersiveconvergence$pointOf(con)); }

    @Override public void removeCable(Connection connection) {
        if (connection == null) {
            ICWireSavedData.ieWireRemoved(world, pos, null, null);
            IEWireGraph.invalidate(world);
            return;
        }
        BlockPos otherEnd = immersiveconvergence$otherEnd(connection);
        ICWireType type = IEWireBridge.required(connection.cableType);
        ICConnectionPoint point = immersiveconvergence$pointOf(connection);
        immersiveconvergence$self().onWireRemoved(point, immersiveconvergence$wire(point, otherEnd, type), IEWireEvents.cause());
        ICWireSavedData.ieWireRemoved(world, pos, otherEnd, type);
        IEWireGraph.invalidate(world);
    }

    @Override @Nonnull public Vec3d getConnectionOffset(@Nonnull Connection con) { return immersiveconvergence$self().offset(immersiveconvergence$pointOf(con), IEWireBridge.required(con.cableType)); }

    @Override @Nonnull public Vec3d getConnectionOffset(@Nonnull Connection con, TargetingInfo target, Vec3i offsetLink) {
        ICConnectionPoint point = immersiveconvergence$self().pointAt(IEWireBridge.targeting(target), offsetLink);
        return immersiveconvergence$self().offset(point != null ? point : immersiveconvergence$pointOf(con), IEWireBridge.required(con.cableType));
    }

    @Override public void addAvailableEnergy(float amount, Consumer<Float> consume) {
        long currentTime = world.getTotalWorldTime();
        if (immersiveconvergence$lastSourceUpdate != currentTime) {
            immersiveconvergence$sources.clear();
            immersiveconvergence$lastSourceUpdate = currentTime;
        }
        if (amount > 0 && consume != null) { immersiveconvergence$sources.add(new ImmutablePair<>(amount, consume)); }
    }

    @Override public float getDamageAmount(Entity e, Connection c) {
        float baseDmg = immersiveconvergence$baseDamage(c);
        float max = c.cableType.getTransferRate() / 8F * baseDmg;
        if (baseDmg == 0 || world.getTotalWorldTime() - immersiveconvergence$lastSourceUpdate > 1) { return 0; }
        float damage = 0;
        for (int i = 0; i < immersiveconvergence$sources.size() && damage < max; i++) {
            int consume = (int)Math.min(immersiveconvergence$sources.get(i).getLeft(), (max - damage) / baseDmg);
            damage += baseDmg * consume;
        }
        return damage;
    }

    @Override public void processDamage(Entity e, float amount, Connection c) {
        float baseDmg = immersiveconvergence$baseDamage(c);
        float damage = 0;
        for (int i = 0; i < immersiveconvergence$sources.size() && damage < amount; i++) {
            float consume = Math.min(immersiveconvergence$sources.get(i).getLeft(), (amount - damage) / baseDmg);
            immersiveconvergence$sources.get(i).getRight().accept(consume);
            damage += baseDmg * consume;
            if (consume == immersiveconvergence$sources.get(i).getLeft()) {
                immersiveconvergence$sources.remove(i);
                i--;
            }
        }
    }

    @Unique private float immersiveconvergence$baseDamage(Connection c) {
        if (c.cableType == WireType.COPPER) { return 8 * 2F / c.cableType.getTransferRate(); }
        if (c.cableType == WireType.ELECTRUM) { return 8 * 5F / c.cableType.getTransferRate(); }
        if (c.cableType == WireType.STEEL) { return 8 * 15F / c.cableType.getTransferRate(); }
        if (c.cableType instanceof ICIEWireType) { return 8 * ((ICIEWireType)c.cableType).getDamageFactor() / c.cableType.getTransferRate(); }
        return 0;
    }



    @Inject(method = "readCustomNBT", at = @At("TAIL")) private void immersiveconvergence$readConnections(NBTTagCompound nbt, boolean descPacket, CallbackInfo ci) {
        if (nbt.hasKey("connectionList")) { immersiveconvergence$readConns(nbt); }
    }

    @Inject(method = "writeCustomNBT", at = @At("TAIL")) private void immersiveconvergence$writeConnections(NBTTagCompound nbt, boolean descPacket, CallbackInfo ci) {
        if (descPacket) { immersiveconvergence$writeConns(nbt); }
    }

    @Unique private void immersiveconvergence$readConns(NBTTagCompound nbt) {
        if (world != null && world.isRemote && ImmersiveConvergence.proxy.isMultiplayerClient() && nbt != null) {
            NBTTagList connectionList = nbt.getTagList("connectionList", 10);
            ImmersiveNetHandler.INSTANCE.clearConnectionsOriginatingFrom(pos, world);
            for (int i = 0; i < connectionList.tagCount(); i++) {
                Connection con = Connection.readFromNBT(connectionList.getCompoundTagAt(i));
                if (con != null) { ImmersiveNetHandler.INSTANCE.addConnection(world, pos, con); }
            }
        }
    }

    @Unique private void immersiveconvergence$writeConns(NBTTagCompound nbt) {
        if (world != null && !world.isRemote && nbt != null) {
            NBTTagList connectionList = new NBTTagList();
            Set<Connection> conL = ImmersiveNetHandler.INSTANCE.getConnections(world, pos);
            if (conL != null) {
                for (Connection con : conL) { connectionList.appendTag(con.writeToNBT()); }
            }
            nbt.setTag("connectionList", connectionList);
        }
    }

    @Inject(method = "wireConnections", at = @At("HEAD"), cancellable = true) private void immersiveconvergence$wireConnections(CallbackInfoReturnable<Set<?>> cir) { cir.setReturnValue(immersiveconvergence$genConns()); }

    @Unique private Set<Connection> immersiveconvergence$genConns() {
        Set<Connection> conns = ImmersiveNetHandler.INSTANCE.getConnections(world, pos);
        if (conns == null) { return ImmutableSet.of(); }
        List<Connection> ret = new ArrayList<>();
        for (Connection c : conns) {
            if (ApiUtils.toIIC(c.end, world, false) == null) { continue; }
            c.getSubVertices(world);
            ret.add(c);
        }
        return new AbstractSet<Connection>() {
            @Override @Nonnull public Iterator<Connection> iterator() { return ret.iterator(); }

            @Override public int size() { return ret.size(); }
        };
    }

    @Override public void onChunkUnload() {
        super.onChunkUnload();
        if (!ICMods.immersiveEngineering()) { return; }
        if (!world.isRemote) { ImmersiveNetHandler.INSTANCE.addProxy(new IICProxy(this)); }
    }

    @Override public void validate() {
        super.validate();
        if (!ICMods.immersiveEngineering()) { return; }
        if (!world.isRemote) { ApiUtils.addFutureServerTask(world, () -> ImmersiveNetHandler.INSTANCE.onTEValidated(this)); }
    }
}
