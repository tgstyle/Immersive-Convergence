package com.immersiveconvergence.mixin.integration.immersiveengineering.common.blocks.stone;

import com.immersiveconvergence.core.integration.immersiveengineering.IEMultiblockRegistry;
import com.immersiveconvergence.core.integration.immersiveengineering.util.IEMultiblockHooks;
import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import blusunrize.immersiveengineering.common.blocks.stone.TileEntityBlastFurnaceAdvanced;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityBlastFurnaceAdvanced.class) public abstract class MixinTileEntityBlastFurnaceAdvanced {
    @Inject(method = "getBlockBounds", at = @At("HEAD"), cancellable = true, remap = false, require = 0) private void injectGetBlockBounds(CallbackInfoReturnable<float[]> cir) { IEMultiblockHooks.blockBounds((TileEntityMultiblockPart<?>)(Object)this, cir); }

    @Redirect(method = "getCapability", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/stone/TileEntityBlastFurnaceAdvanced;pos:I", remap = true), remap = false) private int redirectPosGetCapability(TileEntityBlastFurnaceAdvanced part) { return IEMultiblockRegistry.portPos(part); }

    @Redirect(method = "hasCapability", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/stone/TileEntityBlastFurnaceAdvanced;pos:I", remap = true), remap = false) private int redirectPosHasCapability(TileEntityBlastFurnaceAdvanced part) { return IEMultiblockRegistry.portPos(part); }

    @Inject(method = "getOriginalBlock", at = @At("HEAD"), cancellable = true, remap = false, require = 0) private void injectGetOriginalBlock(CallbackInfoReturnable<ItemStack> cir) { IEMultiblockHooks.originalBlock((TileEntityMultiblockPart<?>)(Object)this, cir); }
}
