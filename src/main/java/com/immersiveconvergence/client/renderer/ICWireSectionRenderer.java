package com.immersiveconvergence.client.renderer;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.client.util.ICClientWires;
import com.immersiveconvergence.core.lib.ICLib;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;

import java.util.List;

@SuppressWarnings("unused") @EventBusSubscriber(modid = ICLib.MODID, value = Dist.CLIENT) public final class ICWireSectionRenderer {
    private static final ResourceLocation WIRE_TEXTURE = ResourceLocation.withDefaultNamespace("block/white_concrete");

    private ICWireSectionRenderer() {}

    @SubscribeEvent public static void onAddSectionGeometry(AddSectionGeometryEvent event) {
        if (ICMods.immersiveEngineering()) { return; }
        List<ICWireGeometry.Segment> segments = ICClientWires.getSegmentsIn(SectionPos.of(event.getSectionOrigin()));
        if (!segments.isEmpty()) { event.addRenderer(context -> render(segments, context.getOrCreateChunkBuffer(RenderType.solid()), context.getRegion())); }
    }

    private static void render(List<ICWireGeometry.Segment> segments, VertexConsumer out, BlockAndTintGetter region) {
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(WIRE_TEXTURE);
        float u = sprite.getU(0.5F);
        float v = sprite.getV(0.5F);
        for (ICWireGeometry.Segment segment : segments) { segment.emit(out, region, u, v); }
    }
}
