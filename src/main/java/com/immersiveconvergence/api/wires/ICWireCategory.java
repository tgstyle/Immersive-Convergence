package com.immersiveconvergence.api.wires;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public final class ICWireCategory {
    private static final Map<String, ICWireCategory> REGISTRY = new ConcurrentHashMap<>();
    private static volatile boolean frozen;
    public static final ICWireCategory LV = register("LV", ICWireRole.ENERGY, 1);
    public static final ICWireCategory MV = register("MV", ICWireRole.ENERGY, 3);
    public static final ICWireCategory HV = register("HV", ICWireRole.ENERGY, 6);
    public static final ICWireCategory STRUCTURE = register("STRUCTURE", ICWireRole.STRUCTURE, 0);
    private final String name;
    private final ICWireRole role;
    private final float defaultShock;
    @Nullable private volatile ICWireType defaultType;

    private ICWireCategory(String name, ICWireRole role, float defaultShock) {
        this.name = name;
        this.role = role;
        this.defaultShock = defaultShock;
    }

    @Nonnull public static ICWireCategory register(@Nonnull String name, @Nonnull ICWireRole role, float defaultShock) {
        ICWireCategory known = REGISTRY.get(name);
        if (known != null) { return known; }
        checkOpen(name);
        return REGISTRY.computeIfAbsent(name, key -> new ICWireCategory(key, role, defaultShock));
    }

    @Nullable public static ICWireCategory byName(@Nullable String name) { return name == null ? null : REGISTRY.get(name); }

    @Nonnull public static Collection<ICWireCategory> values() { return Collections.unmodifiableCollection(REGISTRY.values()); }

    public static boolean frozen() { return frozen; }

    static void freeze() { frozen = true; }

    static void checkOpen(String name) {
        if (frozen) { throw new IllegalStateException("Wire registry is frozen after postInit; cannot register " + name); }
    }

    @Nonnull public String getName() { return name; }

    @Nonnull public ICWireRole getRole() { return role; }

    public float getDefaultShock() { return defaultShock; }

    @Nullable public ICWireType getDefaultType() { return defaultType; }

    void setDefaultType(@Nonnull ICWireType type) { defaultType = type; }

    @Override public String toString() { return name; }
}
