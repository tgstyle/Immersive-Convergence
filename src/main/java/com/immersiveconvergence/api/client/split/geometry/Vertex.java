package com.immersiveconvergence.api.client.split.geometry;

public final class Vertex {
    public final Vec3d position;
    public final Vec3d normal;
    public final UVCoords uv;

    public Vertex(Vec3d position, Vec3d normal, UVCoords uv) {
        this.position = position;
        this.normal = normal;
        this.uv = uv;
    }

    public static Vertex interpolate(Vertex a, Vertex b, double lambda) {
        return new Vertex(a.position.scale(lambda).add(b.position.scale(1.0D - lambda)), a.normal.scale(lambda).add(b.normal.scale(1.0D - lambda)), UVCoords.interpolate(a.uv, b.uv, lambda));
    }

    public Vertex translate(int axis, double amount) {
        double[] offsetData = new double[3];
        offsetData[axis] = amount;
        return this.translate(new Vec3d(offsetData));
    }

    public Vertex translate(Vec3d offset) { return new Vertex(this.position.add(offset), this.normal, this.uv); }
}
