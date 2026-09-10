package com.immersiveconvergence.mixin.ie.common;

import com.immersiveconvergence.api.energy.IICFluxAcceptor;
import com.immersiveconvergence.api.energy.IICFluxProvider;

import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.common.items.ItemIETool;
import blusunrize.immersiveengineering.common.util.ChatUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemIETool.class, remap = false)
public abstract class MixinIEToolVoltmeter {

    @Inject(method = "onItemUse", at = @At("HEAD"), cancellable = true)
    private void immersiveconvergence$readICFlux(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ, CallbackInfoReturnable<EnumActionResult> cir) {
        if (world.isRemote || player.isSneaking()) { return; }
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getMetadata() != 2) { return; }
        TileEntity tile = world.getTileEntity(pos);
        int stored;
        int max;
        if (tile instanceof IICFluxAcceptor) {
            stored = ((IICFluxAcceptor)tile).getEnergyStored(side);
            max = ((IICFluxAcceptor)tile).getMaxEnergyStored(side);
        }
        else if (tile instanceof IICFluxProvider) {
            stored = ((IICFluxProvider)tile).getEnergyStored(side);
            max = ((IICFluxProvider)tile).getMaxEnergyStored(side);
        }
        else { return; }
        if (max <= 0) { return; }
        ChatUtils.sendServerNoSpamMessages(player, new TextComponentTranslation(Lib.CHAT_INFO + "energyStorage", stored, max));
        cir.setReturnValue(EnumActionResult.SUCCESS);
    }
}
