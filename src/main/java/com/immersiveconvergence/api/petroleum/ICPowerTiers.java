package com.immersiveconvergence.api.petroleum;

import com.immersiveconvergence.core.ICCommonConfig;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public final class ICPowerTiers {
    private static final List<ICPowerTier> TIERS = new ArrayList<>();

    private ICPowerTiers() {}

    public static int register(int capacity, int usage) {
        ICPowerTier tier = new ICPowerTier(capacity, usage);
        int existing = TIERS.indexOf(tier);
        if (existing >= 0) { return existing; }
        TIERS.add(tier);
        return TIERS.size() - 1;
    }

    public static void define(int id, int capacity, int usage) {
        if (id < 0) { return; }
        while (TIERS.size() <= id) { TIERS.add(null); }
        TIERS.set(id, new ICPowerTier(capacity, usage));
    }

    public static ICPowerTier get(int id) {
        ICPowerTier tier = id >= 0 && id < TIERS.size() ? TIERS.get(id) : null;
        return tier == null ? fallback() : tier;
    }

    public static ICPowerTier fallback() { return new ICPowerTier(ICCommonConfig.petroleum.defaultCapacity, ICCommonConfig.petroleum.defaultUsage); }

    public static int count() { return TIERS.size(); }

    public static void clear() { TIERS.clear(); }
}
