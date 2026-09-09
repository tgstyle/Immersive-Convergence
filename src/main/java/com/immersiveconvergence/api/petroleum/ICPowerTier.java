package com.immersiveconvergence.api.petroleum;

public final class ICPowerTier {
    private final int capacity;
    private final int usage;

    public ICPowerTier(int capacity, int usage) {
        this.capacity = capacity;
        this.usage = usage;
    }

    public int getCapacity() { return capacity; }

    public int getUsage() { return usage; }

    @Override public boolean equals(Object object) {
        if (this == object) { return true; }
        if (!(object instanceof ICPowerTier)) { return false; }
        ICPowerTier other = (ICPowerTier)object;
        return capacity == other.capacity && usage == other.usage;
    }

    @Override public int hashCode() { return 31 * capacity + usage; }

    @Override public String toString() { return capacity + " IF, " + usage + " IF/t"; }
}
