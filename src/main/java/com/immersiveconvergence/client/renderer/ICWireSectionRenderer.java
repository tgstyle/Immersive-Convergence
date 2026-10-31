package com.immersiveconvergence.client.renderer;

import com.immersiveconvergence.client.ICClientWires;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

public final class ICWireSectionRenderer {
    private static final ResourceLocation WIRE_TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft", "block/white_concrete");

    private ICWireSectionRenderer() {}

    public static boolean hasWires(@Nonnull BlockPos inSection) { return !ICClientWires.getSegmentsIn(SectionPos.of(inSection)).isEmpty(); }

    public static void renderSection(@Nonnull BlockPos origin, @Nonnull Set<RenderType> layers, @Nonnull ChunkBufferBuilderPack buffers, @Nonnull BlockAndTintGetter region) {
        List<ICWireGeometry.Segment> segments = ICClientWires.getSegmentsIn(SectionPos.of(origin));
        if (segments.isEmpty()) { return; }
        BufferBuilder builder = buffers.builder(RenderType.solid());
        if (layers.add(RenderType.solid())) { builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK); }
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(WIRE_TEXTURE);
        float u = sprite.getU(8);
        float v = sprite.getV(8);
        for (ICWireGeometry.Segment segment : segments) { segment.emit(builder, region, u, v); }
    }
}
