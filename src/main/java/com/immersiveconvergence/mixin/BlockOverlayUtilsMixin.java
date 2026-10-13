package com.immersiveconvergence.mixin;

import com.immersiveconvergence.api.block.IMBOverlayText;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelper;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockBE;
import blusunrize.immersiveengineering.client.BlockOverlayUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = BlockOverlayUtils.class, remap = false)
public abstract class BlockOverlayUtilsMixin {
    @SuppressWarnings("unchecked") @Inject(method = "renderMultiblockOverlay(Lnet/minecraft/client/gui/GuiGraphics;Lblusunrize/immersiveengineering/api/multiblocks/blocks/logic/IMultiblockBE;Lnet/minecraft/world/phys/BlockHitResult;ZII)Z", at = @At("HEAD"), cancellable = true, remap = false) private static void ic$icOverlayText(GuiGraphics graphics, IMultiblockBE<?> be, BlockHitResult absoluteHit, boolean hammer, int scaledWidth, int scaledHeight, CallbackInfoReturnable<Boolean> cir) {
        IMultiblockBEHelper<?> helper = be.getHelper();
        if (!(helper.getMultiblock().logic() instanceof IMBOverlayText<?> handler)) { return; }
        List<Component> overlayText = ((IMBOverlayText<Object>)handler).getOverlayText(helper.getState(), helper.getPositionInMB(), absoluteHit, Minecraft.getInstance().player, hammer);
        if (overlayText == null) {
            cir.setReturnValue(false);
            return;
        }
        BlockOverlayUtils.drawBlockOverlayText(graphics, overlayText, scaledWidth, scaledHeight);
        cir.setReturnValue(true);
    }
}
