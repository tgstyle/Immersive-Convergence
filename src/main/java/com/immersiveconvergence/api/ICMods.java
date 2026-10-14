package com.immersiveconvergence.api;

import com.immersiveconvergence.core.lib.ICLib;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.toml.TomlFormat;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;

public final class ICMods {
    public static final String IMMERSIVE_ENGINEERING = "immersiveengineering";
    public static final String IMMERSIVE_PETROLEUM = "immersivepetroleum";
    public static final String COMPUTERCRAFT = "computercraft";
    private static final String DISABLE_IMMERSIVE_ENGINEERING = "experimental.disable_immersive_engineering";
    private static boolean immersiveEngineering;
    private static boolean immersivePetroleum;
    private static volatile boolean initialized;

    private ICMods() {}

    private static synchronized void init() {
        if (initialized) { return; }
        boolean disabled = disableImmersiveEngineering();
        if (disabled) { ICLib.IC_LOGGER.warn("Experimental: disable_immersive_engineering is on, so Immersive Engineering and Immersive Petroleum are being treated as absent. Their recipes, multiblocks and placed blocks will not work. You said you can replace the needed recipes and NBT data yourself."); }
        immersiveEngineering = !disabled && ModList.get().isLoaded(IMMERSIVE_ENGINEERING);
        immersivePetroleum = immersiveEngineering && ModList.get().isLoaded(IMMERSIVE_PETROLEUM);
        initialized = true;
    }

    private static boolean disableImmersiveEngineering() {
        try {
            Config config = TomlFormat.instance().createParser().parse(FMLPaths.CONFIGDIR.get().resolve(ICLib.MODID + "-common.toml"), FileNotFoundAction.READ_NOTHING);
            return config.getOrElse(DISABLE_IMMERSIVE_ENGINEERING, false);
        }
        catch (ParsingException e) { return false; }
    }

    private static void ensureInitialized() { if (!initialized) { init(); } }

    public static boolean immersiveEngineering() { ensureInitialized(); return immersiveEngineering; }

    public static boolean immersivePetroleum() { ensureInitialized(); return immersivePetroleum; }

    public static boolean computerCraft() { return ModList.get().isLoaded(COMPUTERCRAFT); }
}
