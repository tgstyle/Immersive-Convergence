package com.immersiveconvergence.api.client.split.geometry;

public final class ModPlane {
    public final Vec3d normal;
    public final double dotProduct;

    public ModPlane(Vec3d normal, double dotProduct) {
        this.normal = normal;
        this.dotProduct = dotProduct;
    }
}
