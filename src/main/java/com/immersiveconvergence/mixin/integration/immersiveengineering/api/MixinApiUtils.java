package com.immersiveconvergence.mixin.integration.immersiveengineering.api;

import com.immersiveconvergence.common.energy.IEWireEvents;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.energy.wires.IWireCoil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ApiUtils.class, remap = false) public abstract class MixinApiUtils {
    @Inject(method = "doCoilUse", at = @At(value = "INVOKE", target = "Lblusunrize/immersiveengineering/api/energy/wires/ImmersiveNetHandler;addAndGetConnection(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;ILblusunrize/immersiveengineering/api/energy/wires/WireType;)Lblusunrize/immersiveengineering/api/energy/wires/ImmersiveNetHandler$Connection;"), cancellable = true) private static void immersiveconvergence$askPlaced(IWireCoil coil, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ, CallbackInfoReturnable<EnumActionResult> cir) {
        if (IEWireEvents.refusedCoilUse(coil, player, world, pos, hand, side, hitX, hitY, hitZ)) { cir.setReturnValue(EnumActionResult.SUCCESS); }
    }
}
