package com.immersiveconvergence.api.client.split.geometry;

import com.google.common.base.Preconditions;

@SuppressWarnings("unused")
public final class Vec3d {
    public static final Vec3d ZERO = new Vec3d(0.0D, 0.0D, 0.0D);
    public final double x, y, z;

    public Vec3d(double x, double y, double z) {
        Preconditions.checkArgument(Double.isFinite(x));
        Preconditions.checkArgument(Double.isFinite(y));
        Preconditions.checkArgument(Double.isFinite(z));
        this.x = x; this.y = y; this.z = z;
    }

    public Vec3d(double[] coords) { this(coords[0], coords[1], coords[2]); }

    public Vec3d(ModelSplitterVec3i vec) { this(vec.x, vec.y, vec.z); }

    public double dotProduct(Vec3d other) { return this.x * other.x + this.y * other.y + this.z * other.z; }

    public double get(int index) {
        switch (index) {
            case 0: return this.x;
            case 1: return this.y;
            case 2: return this.z;
            default: throw new IllegalStateException("Unexpected index in Vec3d: " + index);
        }
    }

    public Vec3d normalize() {
        double length = this.length();
        return length < 1.0E-4D ? this : this.scale(1.0D / length);
    }

    public double length() { return Math.sqrt(this.lengthSquared()); }

    public double lengthSquared() { return this.x * this.x + this.y * this.y + this.z * this.z; }

    public Vec3d scale(double lambda) { return new Vec3d(this.x * lambda, this.y * lambda, this.z * lambda); }

    public Vec3d add(Vec3d other) { return new Vec3d(this.x + other.x, this.y + other.y, this.z + other.z); }

    public Vec3d subtract(Vec3d other) { return new Vec3d(this.x - other.x, this.y - other.y, this.z - other.z); }
}
