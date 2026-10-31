package com.immersiveconvergence.client.renderer;

import com.immersiveconvergence.api.wires.ICWire;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ICWireGeometry {
    private static final int[][] CORNERS = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
    private static final Vec3 UP = new Vec3(0, 1, 0);
    private static final Vec3 EAST = new Vec3(1, 0, 0);

    private ICWireGeometry() {}

    public static int segments(@Nonnull ICWire wire) { return 2 * Mth.clamp((int)Math.ceil(wire.getLength() / 4), 2, 12); }

    @Nonnull public static List<Segment> build(@Nonnull ICWire wire, @Nonnull Vec3[] points) {
        int count = points.length - 1;
        Vec3 reference = Math.abs(points[0].x - points[count].x) + Math.abs(points[0].z - points[count].z) < 0.01 ? EAST : UP;
        double radius = wire.getType().getRenderRadius();
        Vec3[][] rings = new Vec3[count + 1][];
        for (int i = 0; i <= count; i++) { rings[i] = ring(points, i, reference, radius); }
        List<Segment> segments = new ArrayList<>(count);
        for (int s = 0; s < count; s++) { split(points[s], points[s + 1], rings[s], rings[s + 1], wire.getType().colorFor(wire), segments); }
        return segments;
    }

    private static void split(Vec3 start, Vec3 end, Vec3[] first, Vec3[] second, int color, List<Segment> out) {
        Vec3[] outward = new Vec3[4];
        for (int k = 0; k < 4; k++) { outward[k] = first[k].add(first[(k + 1) % 4]).scale(0.5).subtract(start).normalize(); }
        double[] cuts = cuts(start, end);
        for (int c = 0; c + 1 < cuts.length; c++) {
            if (cuts[c + 1] - cuts[c] < 1.0E-6) { continue; }
            out.add(new Segment(start.lerp(end, cuts[c]), start.lerp(end, cuts[c + 1]), lerp(first, second, cuts[c]), lerp(first, second, cuts[c + 1]), outward, color));
        }
    }

    private static double[] cuts(Vec3 start, Vec3 end) {
        double[] cuts = new double[2 + crossings(start.x, end.x) + crossings(start.y, end.y) + crossings(start.z, end.z)];
        int at = axis(start.x, end.x, cuts, 1);
        at = axis(start.y, end.y, cuts, at);
        axis(start.z, end.z, cuts, at);
        cuts[cuts.length - 1] = 1;
        Arrays.sort(cuts);
        return cuts;
    }

    private static int crossings(double from, double to) { return Math.abs(SectionPos.blockToSectionCoord(to) - SectionPos.blockToSectionCoord(from)); }

    private static int axis(double from, double to, double[] cuts, int at) {
        int a = SectionPos.blockToSectionCoord(from);
        int b = SectionPos.blockToSectionCoord(to);
        for (int section = Math.min(a, b) + 1; section <= Math.max(a, b); section++) { cuts[at++] = (SectionPos.sectionToBlockCoord(section) - from) / (to - from); }
        return at;
    }

    private static Vec3[] lerp(Vec3[] first, Vec3[] second, double delta) {
        Vec3[] ring = new Vec3[4];
        for (int k = 0; k < 4; k++) { ring[k] = first[k].lerp(second[k], delta); }
        return ring;
    }

    private static Vec3[] ring(Vec3[] points, int index, Vec3 reference, double radius) {
        Vec3 tangent = points[Math.min(index + 1, points.length - 1)].subtract(points[Math.max(index - 1, 0)]).normalize();
        Vec3 first = tangent.cross(reference);
        if (first.lengthSqr() < 1.0E-8) { first = tangent.cross(EAST); }
        first = first.normalize();
        Vec3 second = tangent.cross(first).normalize();
        Vec3[] ring = new Vec3[4];
        for (int k = 0; k < 4; k++) { ring[k] = points[index].add(first.scale(radius * CORNERS[k][0])).add(second.scale(radius * CORNERS[k][1])); }
        return ring;
    }

    public static final class Segment {
        private final SectionPos section;
        private final BlockPos light;
        private final float[] vertices = new float[48];
        private final float[] normals = new float[12];
        private final Direction[] faces = new Direction[4];
        private final int color;

        private Segment(Vec3 start, Vec3 end, Vec3[] first, Vec3[] second, Vec3[] outwards, int color) {
            this.light = BlockPos.containing(start.add(end).scale(0.5));
            this.section = SectionPos.of(light);
            this.color = color;
            Vec3 origin = Vec3.atLowerCornerOf(section.origin());
            for (int k = 0; k < 4; k++) {
                int next = (k + 1) % 4;
                Vec3[] corners = {first[k], first[next], second[next], second[k]};
                Vec3 outward = outwards[k];
                if (corners[1].subtract(corners[0]).cross(corners[3].subtract(corners[0])).dot(outward) < 0) { corners = new Vec3[]{corners[0], corners[3], corners[2], corners[1]}; }
                for (int v = 0; v < 4; v++) {
                    Vec3 corner = corners[v].subtract(origin);
                    vertices[k * 12 + v * 3] = (float)corner.x;
                    vertices[k * 12 + v * 3 + 1] = (float)corner.y;
                    vertices[k * 12 + v * 3 + 2] = (float)corner.z;
                }
                normals[k * 3] = (float)outward.x;
                normals[k * 3 + 1] = (float)outward.y;
                normals[k * 3 + 2] = (float)outward.z;
                faces[k] = Direction.getNearest(outward.x, outward.y, outward.z);
            }
        }

        @Nonnull public SectionPos getSection() { return section; }

        public void emit(@Nonnull VertexConsumer out, @Nonnull BlockAndTintGetter region, float u, float v) {
            int packedLight = LevelRenderer.getLightColor(region, light);
            for (int k = 0; k < 4; k++) {
                float shade = region.getShade(faces[k], true) / 255F;
                float red = (color >> 16 & 255) * shade;
                float green = (color >> 8 & 255) * shade;
                float blue = (color & 255) * shade;
                for (int i = 0; i < 4; i++) {
                    int at = k * 12 + i * 3;
                    out.vertex(vertices[at], vertices[at + 1], vertices[at + 2], red, green, blue, 1F, u, v, OverlayTexture.NO_OVERLAY, packedLight, normals[k * 3], normals[k * 3 + 1], normals[k * 3 + 2]);
                }
            }
        }
    }
}
