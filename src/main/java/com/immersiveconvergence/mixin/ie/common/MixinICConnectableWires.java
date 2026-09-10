package com.immersiveconvergence.mixin.ie.common;

import com.immersiveconvergence.api.energy.ICTargetingInfo;
import com.immersiveconvergence.api.energy.ICTileEntityConnectable;
import com.immersiveconvergence.api.energy.ICWireType;
import com.immersiveconvergence.common.energy.IEWireBridge;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.energy.wires.IICProxy;
import blusunrize.immersiveengineering.api.energy.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import blusunrize.immersiveengineering.api.energy.wires.WireType;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.Minecraft;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@Mixin(value = ICTileEntityConnectable.class, remap = false)
public abstract class MixinICConnectableWires extends net.minecraft.tileentity.TileEntity implements IImmersiveConnectable {

    @Unique private final List<Pair<Float, Consumer<Float>>> immersiveconvergence$sources = new ArrayList<>();
    @Unique private long immersiveconvergence$lastSourceUpdate = 0;

    @Unique private ICTileEntityConnectable immersiveconvergence$self() { return (ICTileEntityConnectable)(Object)this; }

    @Unique private BlockPos immersiveconvergence$otherEnd(Connection con) { return con.start.equals(pos) ? con.end : con.start; }

    @Override public BlockPos getConnectionMaster(@Nullable WireType cableType, TargetingInfo target) { return immersiveconvergence$self().connectionMaster(); }

    @Override public boolean canConnectCable(WireType cableType, TargetingInfo target, @Nonnull Vec3i offset) {
        ICWireType cable = IEWireBridge.required(cableType);
        Boolean accepted = immersiveconvergence$self().canConnectCable(cable, IEWireBridge.targeting(target), offset);
        return accepted != null ? accepted : immersiveconvergence$self().acceptsCable(cable);
    }

    @Override public void connectCable(WireType cableType, TargetingInfo target, IImmersiveConnectable other) {
        ICWireType cable = IEWireBridge.required(cableType);
        if (!immersiveconvergence$self().connectCable(cable, IEWireBridge.targeting(target), other.getConnectionMaster(cableType, target))) {
            immersiveconvergence$self().acceptCable(cable);
        }
    }

    @Override public WireType getCableLimiter(@Nonnull TargetingInfo target) {
        return IEWireBridge.toIE(immersiveconvergence$self().cableLimiter(IEWireBridge.targeting(target)));
    }

    @Override public boolean allowEnergyToPass(Connection con) { return immersiveconvergence$self().allowEnergyToPass(); }

    @Override public void removeCable(Connection connection) {
        if (!immersiveconvergence$self().removeCable(connection == null ? null : immersiveconvergence$otherEnd(connection), connection == null)) {
            immersiveconvergence$self().forgetCable(connection == null ? null : IEWireBridge.of(connection.cableType));
        }
    }

    @Override @Nonnull public Vec3d getConnectionOffset(@Nonnull Connection con) {
        return immersiveconvergence$self().connectionOffset(IEWireBridge.required(con.cableType), immersiveconvergence$otherEnd(con));
    }

    @Override @Nonnull public Vec3d getConnectionOffset(@Nonnull Connection con, TargetingInfo target, Vec3i offsetLink) {
        ICTargetingInfo info = IEWireBridge.targeting(target);
        return immersiveconvergence$self().connectionOffset(IEWireBridge.required(con.cableType), info);
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
        return 0;
    }



    @Inject(method = "readCustomNBT", at = @At("TAIL"))
    private void immersiveconvergence$readConnections(NBTTagCompound nbt, boolean descPacket, CallbackInfo ci) {
        if (nbt.hasKey("connectionList")) { immersiveconvergence$readConns(nbt); }
    }

    @Inject(method = "writeCustomNBT", at = @At("TAIL"))
    private void immersiveconvergence$writeConnections(NBTTagCompound nbt, boolean descPacket, CallbackInfo ci) {
        if (descPacket) { immersiveconvergence$writeConns(nbt); }
    }

    @Unique private void immersiveconvergence$readConns(NBTTagCompound nbt) {
        if (world != null && world.isRemote && !Minecraft.getMinecraft().isSingleplayer() && nbt != null) {
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

    @Inject(method = "wireConnections", at = @At("HEAD"), cancellable = true)
    private void immersiveconvergence$wireConnections(CallbackInfoReturnable<Set<?>> cir) { cir.setReturnValue(immersiveconvergence$genConns()); }

    @Unique private Set<Connection> immersiveconvergence$genConns() {
        Set<Connection> conns = ImmersiveNetHandler.INSTANCE.getConnections(world, pos);
        if (conns == null) { return ImmutableSet.of(); }
        Set<Connection> ret = new HashSet<Connection>() {
            @Override public boolean equals(Object o) {
                if (o == this) { return true; }
                if (!(o instanceof HashSet)) { return false; }
                HashSet<?> other = (HashSet<?>)o;
                if (other.size() != this.size()) { return false; }
                for (Connection c : this) {
                    if (!other.contains(c)) { return false; }
                }
                return true;
            }
        };
        for (Connection c : conns) {
            if (ApiUtils.toIIC(c.end, world, false) == null) { continue; }
            c.getSubVertices(world);
            ret.add(c);
        }
        return ret;
    }


    @Override public void onLoad() {
        super.onLoad();
        if (world.isRemote) { Minecraft.getMinecraft().addScheduledTask(this::immersiveconvergence$refreshWires); }
    }

    @Unique private void immersiveconvergence$refreshWires() {
        if (world == null || isInvalid()) { return; }
        Set<Connection> conns = ImmersiveNetHandler.INSTANCE.getConnections(world, pos);
        if (conns == null || conns.isEmpty()) { return; }
        for (Connection con : conns) {
            if (!world.isBlockLoaded(con.end)) { continue; }
            con.catenaryVertices = null;
            world.markBlockRangeForRenderUpdate(con.end, con.end);
        }
        world.markBlockRangeForRenderUpdate(pos, pos);
    }

    @Override public void onChunkUnload() {
        super.onChunkUnload();
        if (!world.isRemote) { ImmersiveNetHandler.INSTANCE.addProxy(new IICProxy(this)); }
    }

    @Override public void validate() {
        super.validate();
        if (!world.isRemote) { ApiUtils.addFutureServerTask(world, () -> ImmersiveNetHandler.INSTANCE.onTEValidated(this)); }
    }

    @Override public void invalidate() {
        super.invalidate();
        if (world.isRemote && !Minecraft.getMinecraft().isSingleplayer()) { ImmersiveNetHandler.INSTANCE.clearAllConnectionsFor(pos, world, this, false); }
    }
}
