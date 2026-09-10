package com.immersiveconvergence.mixin.ie.common;

import com.immersiveconvergence.api.energy.ICTileEntityConnectorRedstone;

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

@Mixin(value = ICTileEntityConnectorRedstone.class, remap = false)
public abstract class MixinICConnectorRedstoneWires extends MixinICConnectableWires implements IRedstoneConnector {

    @Unique private RedstoneWireNetwork immersiveconvergence$wireNetwork = null;

    @Unique private ICTileEntityConnectorRedstone immersiveconvergence$connector() { return (ICTileEntityConnectorRedstone)(Object)this; }

    @Unique private RedstoneWireNetwork immersiveconvergence$network() {
        if (immersiveconvergence$wireNetwork == null) { immersiveconvergence$wireNetwork = new RedstoneWireNetwork().add(this); }
        return immersiveconvergence$wireNetwork;
    }

    @Override public void setNetwork(RedstoneWireNetwork net) { immersiveconvergence$wireNetwork = net; }

    @Override public RedstoneWireNetwork getNetwork() { return immersiveconvergence$network(); }

    @Override public World getConnectorWorld() { return getWorld(); }

    @Inject(method = "networkPower", at = @At("HEAD"), cancellable = true)
    private void immersiveconvergence$power(int channel, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(immersiveconvergence$wireNetwork != null ? immersiveconvergence$wireNetwork.getPowerOutput(channel) : 0);
    }

    @Inject(method = "networkUpdateValues", at = @At("HEAD"))
    private void immersiveconvergence$updateValues(CallbackInfo ci) { immersiveconvergence$network().updateValues(); }

    @Inject(method = "networkDetachAll", at = @At("HEAD"))
    private void immersiveconvergence$detachAll(CallbackInfo ci) { immersiveconvergence$network().removeFromNetwork(null); }

    @Inject(method = "networkLeave", at = @At("HEAD"))
    private void immersiveconvergence$leave(CallbackInfo ci) { immersiveconvergence$network().removeFromNetwork(this); }

    @Inject(method = "networkConnectorsUpdated", at = @At("HEAD"))
    private void immersiveconvergence$connectorsUpdated(CallbackInfo ci) {
        RedstoneWireNetwork.updateConnectors(getPos(), getWorld(), immersiveconvergence$network());
    }

    @Override public void onConnectivityUpdate(BlockPos pos, int dimension) { immersiveconvergence$connector().resetNetworkRefresh(); }

    @SideOnly(Side.CLIENT) @Override @Nonnull
    public AxisAlignedBB getRenderBoundingBox() {
        int inc = WireType.REDSTONE.getMaxLength();
        return new AxisAlignedBB(getPos().getX() - inc, getPos().getY() - inc, getPos().getZ() - inc, getPos().getX() + inc + 1, getPos().getY() + inc + 1, getPos().getZ() + inc + 1);
    }
}
