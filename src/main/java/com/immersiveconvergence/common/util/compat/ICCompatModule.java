package com.immersiveconvergence.common.util.compat;

import com.immersiveconvergence.common.util.ICLogger;
import com.immersiveconvergence.common.util.compat.crafttweaker.ICCraftTweaker;
import com.immersiveconvergence.common.util.compat.top.ICOneProbe;
import com.immersiveconvergence.common.util.compat.waila.ICWailaHelper;

import net.minecraftforge.fml.common.Loader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class ICCompatModule {
    private static final Map<String, Class<? extends ICCompatModule>> CANDIDATES = new LinkedHashMap<>();
    private static final List<ICCompatModule> ACTIVE = new ArrayList<>();

    static {
        CANDIDATES.put("crafttweaker", ICCraftTweaker.class);
        CANDIDATES.put("theoneprobe", ICOneProbe.class);
        CANDIDATES.put("waila", ICWailaHelper.class);
    }

    public static void preInitAll() {
        for (Map.Entry<String, Class<? extends ICCompatModule>> candidate : CANDIDATES.entrySet()) {
            if (!Loader.isModLoaded(candidate.getKey())) { continue; }
            try {
                ICCompatModule module = candidate.getValue().newInstance();
                ACTIVE.add(module);
                module.preInit();
            }
            catch (Exception e) { ICLogger.error("Compat module for " + candidate.getKey() + " failed to pre-initialise: " + e); }
        }
    }

    public static void initAll() {
        for (ICCompatModule module : ACTIVE) {
            try { module.init(); }
            catch (Exception e) { ICLogger.error("Compat module " + module.getClass().getSimpleName() + " failed to initialise: " + e); }
        }
    }

    public abstract void preInit();

    public void init() {}
}
