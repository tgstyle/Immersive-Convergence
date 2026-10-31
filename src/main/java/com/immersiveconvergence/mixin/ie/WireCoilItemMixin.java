package com.immersiveconvergence.mixin.ie;

import com.immersiveconvergence.common.ie.IEWireEvents;

import blusunrize.immersiveengineering.api.wires.IWireCoil;
import blusunrize.immersiveengineering.common.items.WireCoilItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = WireCoilItem.class, remap = false) public abstract class WireCoilItemMixin {
    @Inject(method = "doCoilUse", at = @At(value = "INVOKE", target = "Lblusunrize/immersiveengineering/api/wires/GlobalWireNetwork;addConnection(Lblusunrize/immersiveengineering/api/wires/Connection;)V"), cancellable = true)
    private static void immersiveconvergence$askPlaced(IWireCoil coil, Player player, Level world, BlockPos pos, InteractionHand hand, Direction side, float hitX, float hitY, float hitZ, CallbackInfoReturnable<InteractionResult> cir) {
        if (IEWireEvents.refusedCoilUse(coil, player, world, pos, hand, side, hitX, hitY, hitZ)) { cir.setReturnValue(InteractionResult.SUCCESS); }
    }
}
