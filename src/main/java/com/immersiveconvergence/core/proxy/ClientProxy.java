package com.immersiveconvergence.core.proxy;

import com.immersiveconvergence.api.block.BlockInterfaces;
import com.immersiveconvergence.api.client.StandaloneModel;
import com.immersiveconvergence.client.gui.RotorCreativeScreen;
import com.immersiveconvergence.client.models.ICRotorModels;
import com.immersiveconvergence.client.renderer.RotorCreativeRenderer;
import com.immersiveconvergence.core.lib.ICLib;
import com.immersiveconvergence.core.registration.ICBlockEntities;
import com.immersiveconvergence.core.registration.ICMenuTypes;

import blusunrize.immersiveengineering.api.IEApi;
import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.lib.manual.ManualEntry;
import blusunrize.lib.manual.ManualInstance;
import blusunrize.lib.manual.Tree.InnerNode;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import java.util.List;

@EventBusSubscriber(modid = ICLib.MODID, value = Dist.CLIENT)
public class ClientProxy extends CommonProxy {

    private static final List<String> MANUAL_ENTRIES = List.of("multiblock_disassembly", "clearing_tanks");

    @SubscribeEvent public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ManualInstance manual = ManualHelper.getManual();
            InnerNode<ResourceLocation, ManualEntry> construction = manual.getRoot().getOrCreateSubnode(IEApi.ieLoc(ManualHelper.CAT_CONSTRUCTION), 0);
            for (String name : MANUAL_ENTRIES) {
                ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(manual);
                builder.readFromFile(ICLib.rl(name));
                manual.addEntry(construction, builder.create());
            }
        });
    }

    @SubscribeEvent public static void registerModelLoaders(ModelEvent.RegisterGeometryLoaders event) {
        ICRotorModels.ROTOR_CREATIVE = new StandaloneModel(ICLib.rl("dynamic/rotor_creative"));
        ICRotorModels.ROTOR_CREATIVE_EAST_WEST = new StandaloneModel(ICLib.rl("dynamic/rotor_creative_east_west"));
    }

    @SubscribeEvent public static void registerMenuScreens(RegisterMenuScreensEvent event) { event.register(ICMenuTypes.ROTOR_CREATIVE.getType(), RotorCreativeScreen::new); }

    @SubscribeEvent public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) { event.registerBlockEntityRenderer(ICBlockEntities.ROTOR_CREATIVE.get(), ctx -> new RotorCreativeRenderer()); }

    @SubscribeEvent public static void registerOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.SELECTED_ITEM_NAME, ICLib.rl("overlay_text"), (guiGraphics, deltaTracker) -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen != null || mc.player == null) { return; }
            HitResult mop = mc.hitResult;
            if (!(mop instanceof BlockHitResult blockHit)) { return; }
            Level level = mc.level;
            if (level == null) { return; }
            BlockEntity te = level.getBlockEntity(blockHit.getBlockPos());
            if (te instanceof BlockInterfaces.IBlockOverlayText overlay) {
                Component[] text = overlay.getOverlayText(mc.player, mop, false);
                if (text != null && text.length > 0) { drawOverlayText(guiGraphics, text); }
            }
        });
    }

    private static void drawOverlayText(GuiGraphics guiGraphics, Component[] text) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(width / 2f, height / 2f + 30, 0);
        for (int i = 0; i < text.length; i++) {
            String s = text[i].getString();
            int lineWidth = font.width(s);
            float x = -lineWidth / 2f;
            int y = i * (font.lineHeight + 5);
            guiGraphics.fill((int) x - 4, y - 2, (int) x + lineWidth + 4, y + font.lineHeight + 2, 0xAA000000);
            guiGraphics.drawString(font, s, (int) x, y, 0xFFFFFF, true);
        }
        pose.popPose();
    }
}
