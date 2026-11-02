package com.immersiveconvergence.client.models;

import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.client.util.ICClientWires;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;

import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;

public final class ICWireGeometry {
    private static final int[][] CORNERS = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
    private static final Vec3d UP = new Vec3d(0, 1, 0);
    private static final Vec3d EAST = new Vec3d(1, 0, 0);

    private ICWireGeometry() {}

    @Nonnull public static List<BakedQuad> build(@Nonnull Set<?> ends, @Nonnull VertexFormat format, @Nonnull TextureAtlasSprite sprite) {
        ImmutableList.Builder<BakedQuad> quads = ImmutableList.builder();
        for (Object end : ends) {
            if (end instanceof ICWireEnd) { addHalf((ICWireEnd)end, format, sprite, quads); }
        }
        return quads.build();
    }

    private static void addHalf(ICWireEnd end, VertexFormat format, TextureAtlasSprite sprite, ImmutableList.Builder<BakedQuad> quads) {
        ICWire wire = end.getWire();
        int half = MathHelper.clamp((int)Math.ceil(wire.getLength() / 4), 2, 12);
        Vec3d[] points = ICClientWires.getCatenary(wire, half * 2);
        Vec3d origin = new Vec3d(end.getPos());
        int from = end.isStart() ? 0 : half;
        Vec3d reference = Math.abs(points[0].x - points[half * 2].x) + Math.abs(points[0].z - points[half * 2].z) < 0.01 ? EAST : UP;
        double radius = wire.getType().getRenderDiameter() / 2;
        int color = end.getColor();
        float[] rgb = {(color >> 16 & 255) / 255F, (color >> 8 & 255) / 255F, (color & 255) / 255F};
        Vec3d[][] rings = new Vec3d[half + 1][];
        for (int i = 0; i <= half; i++) { rings[i] = ring(points, from + i, reference, radius, origin); }
        for (int s = 0; s < half; s++) {
            Vec3d center = points[from + s].subtract(origin);
            for (int k = 0; k < 4; k++) {
                int next = (k + 1) % 4;
                Vec3d[] corners = {rings[s][k], rings[s][next], rings[s + 1][next], rings[s + 1][k]};
                Vec3d outward = rings[s][k].add(rings[s][next]).scale(0.5).subtract(center).normalize();
                if (corners[1].subtract(corners[0]).crossProduct(corners[3].subtract(corners[0])).dotProduct(outward) < 0) { corners = new Vec3d[]{corners[0], corners[3], corners[2], corners[1]}; }
                quads.add(quad(format, sprite, corners, outward, rgb));
            }
        }
    }

    private static Vec3d[] ring(Vec3d[] points, int index, Vec3d reference, double radius, Vec3d origin) {
        Vec3d tangent = points[Math.min(index + 1, points.length - 1)].subtract(points[Math.max(index - 1, 0)]).normalize();
        Vec3d first = tangent.crossProduct(reference);
        if (first.lengthSquared() < 1.0E-8) { first = tangent.crossProduct(EAST); }
        first = first.normalize();
        Vec3d second = tangent.crossProduct(first).normalize();
        Vec3d center = points[index].subtract(origin);
        Vec3d[] ring = new Vec3d[4];
        for (int k = 0; k < 4; k++) { ring[k] = center.add(first.scale(radius * CORNERS[k][0])).add(second.scale(radius * CORNERS[k][1])); }
        return ring;
    }

    private static BakedQuad quad(VertexFormat format, TextureAtlasSprite sprite, Vec3d[] corners, Vec3d normal, float[] rgb) {
        UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
        builder.setTexture(sprite);
        builder.setQuadOrientation(EnumFacing.getFacingFromVector((float)normal.x, (float)normal.y, (float)normal.z));
        builder.setApplyDiffuseLighting(true);
        for (Vec3d corner : corners) {
            for (int e = 0; e < format.getElementCount(); e++) { put(builder, e, format.getElement(e), corner, normal, rgb, sprite); }
        }
        return builder.build();
    }

    private static void put(UnpackedBakedQuad.Builder builder, int index, VertexFormatElement element, Vec3d corner, Vec3d normal, float[] rgb, TextureAtlasSprite sprite) {
        switch (element.getUsage()) {
            case POSITION:
                builder.put(index, (float)corner.x, (float)corner.y, (float)corner.z, 1);
                break;
            case COLOR:
                builder.put(index, rgb[0], rgb[1], rgb[2], 1);
                break;
            case NORMAL:
                builder.put(index, (float)normal.x, (float)normal.y, (float)normal.z, 0);
                break;
            case UV:
                if (element.getIndex() == 0) { builder.put(index, sprite.getInterpolatedU(8), sprite.getInterpolatedV(8), 0, 1); }
                else { builder.put(index); }
                break;
            default:
                builder.put(index);
                break;
        }
    }
}
