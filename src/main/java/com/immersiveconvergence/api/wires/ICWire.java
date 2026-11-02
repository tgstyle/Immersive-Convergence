package com.immersiveconvergence.api.wires;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused") public final class ICWire {
    private final ICConnectionPoint a;
    private final ICConnectionPoint b;
    private final Vec3 offsetA;
    private final Vec3 offsetB;
    private final ICWireType type;
    private final double length;
    private final List<ChunkPos> chunks;

    public ICWire(@Nonnull ICConnectionPoint first, @Nonnull Vec3 firstOffset, @Nonnull ICConnectionPoint second, @Nonnull Vec3 secondOffset, @Nonnull ICWireType type) {
        boolean ordered = first.compareTo(second) < 0;
        this.a = ordered ? first : second;
        this.b = ordered ? second : first;
        this.offsetA = ordered ? firstOffset : secondOffset;
        this.offsetB = ordered ? secondOffset : firstOffset;
        this.type = type;
        this.length = getEndA().distanceTo(getEndB());
        this.chunks = Collections.unmodifiableList(crossedChunks(getEndA(), getEndB()));
    }

    @Nonnull public ICConnectionPoint getA() { return a; }

    @Nonnull public ICConnectionPoint getB() { return b; }

    @Nonnull public Vec3 getOffsetA() { return offsetA; }

    @Nonnull public Vec3 getOffsetB() { return offsetB; }

    @Nonnull public ICWireType getType() { return type; }

    public double getLength() { return length; }

    @Nonnull public List<ChunkPos> getChunks() { return chunks; }

    @Nonnull public Vec3 getEndA() { return Vec3.atLowerCornerOf(a.position()).add(offsetA); }

    @Nonnull public Vec3 getEndB() { return Vec3.atLowerCornerOf(b.position()).add(offsetB); }

    @Nonnull public Vec3[] getCatenary(int segments) {
        Vec3 start = getEndA();
        Vec3 end = getEndB();
        Vec3[] points = new Vec3[segments + 1];
        double dx = end.x - start.x;
        double dy = end.y - start.y;
        double dz = end.z - start.z;
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        double span = length * type.getSlack();
        double scale = catenaryScale(horizontal, dy, span);
        double vertex = scale > 0 ? horizontal / 2 - scale * 0.5 * Math.log((span + dy) / (span - dy)) : 0;
        for (int i = 0; i <= segments; i++) {
            double t = (double)i / segments;
            double y = scale > 0 ? start.y + scale * (Math.cosh((horizontal * t - vertex) / scale) - Math.cosh(vertex / scale)) : start.y + dy * t;
            points[i] = new Vec3(start.x + dx * t, y, start.z + dz * t);
        }
        points[0] = start;
        points[segments] = end;
        return points;
    }

    private static double catenaryScale(double horizontal, double dy, double span) {
        if (horizontal < 1.0E-3 || span * span - dy * dy <= horizontal * horizontal) { return 0; }
        double ratio = Math.sqrt(span * span - dy * dy) / horizontal;
        double low = 0;
        double high = 1;
        while (Math.sinh(high) / high < ratio) { high *= 2; }
        for (int i = 0; i < 64; i++) {
            double middle = (low + high) / 2;
            if (Math.sinh(middle) / middle < ratio) { low = middle; }
            else { high = middle; }
        }
        return horizontal / (low + high);
    }

    public boolean touches(@Nonnull ICConnectionPoint point) { return a.equals(point) || b.equals(point); }

    @Nonnull public ICConnectionPoint other(@Nonnull ICConnectionPoint point) {
        if (a.equals(point)) { return b; }
        if (b.equals(point)) { return a; }
        throw new IllegalArgumentException(point + " is not an end of " + this);
    }

    @Override public boolean equals(Object other) {
        if (this == other) { return true; }
        if (!(other instanceof ICWire wire)) { return false; }
        return a.equals(wire.a) && b.equals(wire.b) && type.equals(wire.type);
    }

    @Override public int hashCode() { return 31 * (31 * a.hashCode() + b.hashCode()) + type.hashCode(); }

    @Override public String toString() { return a + "-" + b + ":" + type; }

    private static List<ChunkPos> crossedChunks(Vec3 from, Vec3 to) {
        List<ChunkPos> crossed = new ArrayList<>();
        int cx = Math.floorDiv((int)Math.floor(from.x), 16);
        int cz = Math.floorDiv((int)Math.floor(from.z), 16);
        int endX = Math.floorDiv((int)Math.floor(to.x), 16);
        int endZ = Math.floorDiv((int)Math.floor(to.z), 16);
        double dx = to.x - from.x;
        double dz = to.z - from.z;
        int stepX = dx > 0 ? 1 : -1;
        int stepZ = dz > 0 ? 1 : -1;
        double tDeltaX = dx == 0 ? Double.POSITIVE_INFINITY : 16 / Math.abs(dx);
        double tDeltaZ = dz == 0 ? Double.POSITIVE_INFINITY : 16 / Math.abs(dz);
        double tMaxX = dx == 0 ? Double.POSITIVE_INFINITY : ((stepX > 0 ? (cx + 1) * 16 : cx * 16) - from.x) / dx;
        double tMaxZ = dz == 0 ? Double.POSITIVE_INFINITY : ((stepZ > 0 ? (cz + 1) * 16 : cz * 16) - from.z) / dz;
        crossed.add(new ChunkPos(cx, cz));
        while (cx != endX || cz != endZ) {
            if (cx != endX && (cz == endZ || tMaxX < tMaxZ)) {
                cx += stepX;
                tMaxX += tDeltaX;
            }
            else {
                cz += stepZ;
                tMaxZ += tDeltaZ;
            }
            crossed.add(new ChunkPos(cx, cz));
        }
        return crossed;
    }
}
