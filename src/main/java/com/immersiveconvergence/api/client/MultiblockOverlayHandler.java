package com.immersiveconvergence.api.client;

import com.immersiveconvergence.api.block.IMBOverlayText;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockBEHelper;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockBE;
import com.immersiveconvergence.api.util.ICUtils;
import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = ICLib.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class MultiblockOverlayHandler {
    private MultiblockOverlayHandler() {}

    @SubscribeEvent public static void register(RegisterGuiOverlaysEvent event) { event.registerBelow(VanillaGuiOverlay.DEBUG_TEXT.id(), "multiblock_overlay", MultiblockOverlayHandler::render); }

    private static void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || !(mc.hitResult instanceof BlockHitResult hit) || !(player.level().getBlockEntity(hit.getBlockPos()) instanceof IMultiblockBE<?> be)) { return; }
        ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
        List<Component> text = overlayText(be.getHelper(), player, !held.isEmpty() && ICUtils.isHammer(held));
        if (text != null && !text.isEmpty()) { draw(graphics, text, screenWidth, screenHeight, mc.font, mc.renderBuffers().bufferSource()); }
    }

    @SuppressWarnings("unchecked") @Nullable private static <S> List<Component> overlayText(IMultiblockBEHelper<?> helper, LocalPlayer player, boolean hammer) {
        if (!(helper.getMultiblock().logic() instanceof IMBOverlayText<?> handler)) { return null; }
        return ((IMBOverlayText<S>) handler).getOverlayText((S) helper.getState(), player, hammer);
    }

    private static void draw(GuiGraphics graphics, List<Component> text, int scaledWidth, int scaledHeight, Font font, MultiBufferSource.BufferSource buffer) {
        int maxWidth = 0;
        for (Component line : text) { maxWidth = Math.max(maxWidth, font.width(line)); }
        int xPos = scaledWidth / 2 - maxWidth / 2;
        int yPos = scaledHeight / 2 + 16;
        int height = (text.size() == 1 ? -2 : 0) + text.size() * 10;
        TooltipRenderUtil.renderTooltipBackground(graphics, xPos, yPos, maxWidth, height, 0, 0xb01c1d13, 0xb01c1d13, 0x50ff5000, 0x507f2800);
        int i = 0;
        for (Component line : text) { font.drawInBatch(Language.getInstance().getVisualOrder(line), xPos, yPos + (i++) * 10, 0xffffffff, true, graphics.pose().last().pose(), buffer, Font.DisplayMode.NORMAL, 0, 0xf000f0); }
        buffer.endBatch();
    }
}
