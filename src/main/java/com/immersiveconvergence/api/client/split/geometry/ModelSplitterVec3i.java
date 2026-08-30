package com.immersiveconvergence.api.client.split.geometry;

public final class ModelSplitterVec3i {
    public final int x, y, z;

    public ModelSplitterVec3i(int x, int y, int z) {
        this.x = x; this.y = y; this.z = z;
    }

    public int distanceSq(ModelSplitterVec3i other) { return this.subtract(other).lengthSq(); }

    public ModelSplitterVec3i subtract(ModelSplitterVec3i other) { return new ModelSplitterVec3i(this.x - other.x, this.y - other.y, this.z - other.z); }

    public int lengthSq() { return this.x * this.x + this.y * this.y + this.z * this.z; }

    @Override public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof ModelSplitterVec3i)) { return false; }
        ModelSplitterVec3i other = (ModelSplitterVec3i)o;
        return this.x == other.x && this.y == other.y && this.z == other.z;
    }

    @Override public int hashCode() { return (this.x * 31 + this.y) * 31 + this.z; }
}
