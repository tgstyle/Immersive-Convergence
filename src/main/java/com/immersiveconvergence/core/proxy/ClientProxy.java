package com.immersiveconvergence.core.proxy;

import com.immersiveconvergence.api.block.BlockInterfaces;
import com.immersiveconvergence.api.client.StandaloneModel;
import com.immersiveconvergence.client.gui.RotorCreativeScreen;
import com.immersiveconvergence.client.models.ICRotorModels;
import com.immersiveconvergence.client.renderer.RotorCreativeRenderer;
import com.immersiveconvergence.common.blocks.gui.RotorCreativeMenu;
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
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ICLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy extends CommonProxy {
    public static final IGuiOverlay OVERLAY_TEXT = (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
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
    };

    private static final List<String> MANUAL_ENTRIES = List.of("multiblock_disassembly", "clearing_tanks");

    @SubscribeEvent public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ICMenuTypes.ROTOR_CREATIVE.getType(), (RotorCreativeMenu menu, Inventory inv, Component title) -> new RotorCreativeScreen(menu, inv));
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

    @SubscribeEvent public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) { event.registerBlockEntityRenderer(ICBlockEntities.ROTOR_CREATIVE.get(), context -> new RotorCreativeRenderer()); }

    @SubscribeEvent public static void registerOverlays(RegisterGuiOverlaysEvent event) { event.registerAbove(VanillaGuiOverlay.ITEM_NAME.id(), "overlay_text", OVERLAY_TEXT); }

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
