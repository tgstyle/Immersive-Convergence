package com.immersiveconvergence.mixin;

import com.immersiveconvergence.api.block.IMBOverlayText;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelper;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockBE;
import blusunrize.immersiveengineering.client.BlockOverlayUtils;
import blusunrize.immersiveengineering.client.ClientEventHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = ClientEventHandler.class, remap = false)
public abstract class ClientEventHandlerMixin {
    @SuppressWarnings("unchecked") @Inject(method = "renderMultiblockOverlay(Lblusunrize/immersiveengineering/api/multiblocks/blocks/logic/IMultiblockBE;ZLcom/mojang/blaze3d/vertex/PoseStack;II)Z", at = @At("HEAD"), cancellable = true, remap = false) private void ic$icOverlayText(IMultiblockBE<?> be, boolean hammer, PoseStack transform, int scaledWidth, int scaledHeight, CallbackInfoReturnable<Boolean> cir) {
        IMultiblockBEHelper<?> helper = be.getHelper();
        if (!(helper.getMultiblock().logic() instanceof IMBOverlayText<?> handler)) { return; }
        List<Component> overlayText = ((IMBOverlayText<Object>)handler).getOverlayText(helper.getState(), Minecraft.getInstance().player, hammer);
        if (overlayText == null) {
            cir.setReturnValue(false);
            return;
        }
        BlockOverlayUtils.drawBlockOverlayText(transform, overlayText, scaledWidth, scaledHeight);
        cir.setReturnValue(true);
    }
}
