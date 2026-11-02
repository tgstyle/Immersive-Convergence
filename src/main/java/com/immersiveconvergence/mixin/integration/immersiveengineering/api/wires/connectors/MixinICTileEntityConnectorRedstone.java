package com.immersiveconvergence.mixin.integration.immersiveengineering.api.wires.connectors;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.wires.connectors.ICTileEntityConnectorRedstone;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.energy.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import blusunrize.immersiveengineering.api.energy.wires.WireType;
import blusunrize.immersiveengineering.api.energy.wires.redstone.IRedstoneConnector;
import blusunrize.immersiveengineering.api.energy.wires.redstone.RedstoneWireNetwork;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.util.Set;

@Mixin(value = ICTileEntityConnectorRedstone.class, remap = false) public abstract class MixinICTileEntityConnectorRedstone extends MixinICTileEntityConnectable implements IRedstoneConnector {

    @Unique private RedstoneWireNetwork immersiveconvergence$wireNetwork = null;

    @SuppressWarnings("ConstantConditions") @Unique private ICTileEntityConnectorRedstone immersiveconvergence$connector() { return (ICTileEntityConnectorRedstone)(Object)this; }

    @Unique private RedstoneWireNetwork immersiveconvergence$network() {
        if (immersiveconvergence$wireNetwork == null) { immersiveconvergence$wireNetwork = new RedstoneWireNetwork().add(this); }
        return immersiveconvergence$wireNetwork;
    }

    @Override public void setNetwork(RedstoneWireNetwork net) { immersiveconvergence$wireNetwork = net; }

    @Override public RedstoneWireNetwork getNetwork() { return immersiveconvergence$network(); }

    @Override public World getConnectorWorld() { return getWorld(); }

    @Inject(method = "networkPower", at = @At("HEAD"), cancellable = true) private void immersiveconvergence$power(int channel, CallbackInfoReturnable<Integer> cir) {
        if (!ICMods.immersiveEngineering()) { return; }
        cir.setReturnValue(immersiveconvergence$wireNetwork != null ? immersiveconvergence$wireNetwork.getPowerOutput(channel) : 0);
    }

    @Inject(method = "networkUpdateValues", at = @At("HEAD")) private void immersiveconvergence$updateValues(CallbackInfo ci) {
        if (ICMods.immersiveEngineering()) { immersiveconvergence$network().updateValues(); }
    }

    @Inject(method = "networkDetachAll", at = @At("HEAD")) private void immersiveconvergence$detachAll(CallbackInfo ci) {
        if (ICMods.immersiveEngineering()) { immersiveconvergence$join(); }
    }

    @Unique private void immersiveconvergence$join() {
        RedstoneWireNetwork own = immersiveconvergence$network();
        RedstoneWireNetwork joined = null;
        boolean rebuild = false;
        Set<Connection> conns = ImmersiveNetHandler.INSTANCE.getConnections(getWorld(), getPos());
        if (conns != null) {
            for (Connection con : conns) {
                if (!WireType.REDSTONE_CATEGORY.equals(con.cableType.getCategory())) { continue; }
                IImmersiveConnectable other = ApiUtils.toIIC(getPos().equals(con.start) ? con.end : con.start, getWorld(), false);
                if (other == null) { continue; }
                if (!(other instanceof IRedstoneConnector)) {
                    rebuild = true;
                    break;
                }
                RedstoneWireNetwork net = ((IRedstoneConnector)other).getNetwork();
                if (joined == null) { joined = net; }
                else if (joined != net) { rebuild = true; }
            }
        }
        if (rebuild) { RedstoneWireNetwork.updateConnectors(getPos(), getWorld(), own); }
        else if (joined == null || joined == own) { own.updateValues(); }
        else {
            immersiveconvergence$wireNetwork = joined;
            if (!immersiveconvergence$listed(joined)) { joined.add(this); }
            joined.updateValues();
        }
    }

    @Unique private boolean immersiveconvergence$listed(RedstoneWireNetwork net) {
        for (WeakReference<IRedstoneConnector> ref : net.connectors) {
            if (ref.get() == this) { return true; }
        }
        return false;
    }

    @Inject(method = "networkLeave", at = @At("HEAD")) private void immersiveconvergence$leave(CallbackInfo ci) {
        if (ICMods.immersiveEngineering()) { immersiveconvergence$network().removeFromNetwork(this); }
    }

    @Inject(method = "networkConnectorsUpdated", at = @At("HEAD")) private void immersiveconvergence$connectorsUpdated(CallbackInfo ci) {
        if (!ICMods.immersiveEngineering()) { return; }
        RedstoneWireNetwork.updateConnectors(getPos(), getWorld(), immersiveconvergence$network());
    }

    @Override public void onConnectivityUpdate(BlockPos pos, int dimension) { immersiveconvergence$connector().resetNetworkRefresh(); }

    @SideOnly(Side.CLIENT) @Override @Nonnull public AxisAlignedBB getRenderBoundingBox() {
        int inc = WireType.REDSTONE.getMaxLength();
        return new AxisAlignedBB(getPos().getX() - inc, getPos().getY() - inc, getPos().getZ() - inc, getPos().getX() + inc + 1, getPos().getY() + inc + 1, getPos().getZ() + inc + 1);
    }
}
