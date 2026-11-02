package com.immersiveconvergence.api;

import com.immersiveconvergence.core.lib.ICLib;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.toml.TomlFormat;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;

public final class ICMods {
    public static final String IMMERSIVE_ENGINEERING = "immersiveengineering";
    public static final String IMMERSIVE_FABRICATION = "immersivefabrication";
    public static final String IMMERSIVE_PETROLEUM = "immersivepetroleum";
    public static final String COMPUTERCRAFT = "computercraft";
    public static final String OPENCOMPUTERS = "opencomputers";
    private static final String DISABLE_IMMERSIVE_ENGINEERING = "experimental.disable_immersive_engineering";
    private static boolean immersiveEngineering;
    private static boolean ieContent;
    private static boolean immersivePetroleum;
    private static volatile boolean initialized;

    private ICMods() {}

    private static synchronized void init() {
        if (initialized) { return; }
        boolean disabled = disableImmersiveEngineering();
        if (disabled) { ICLib.IC_LOGGER.warn("Experimental: disable_immersive_engineering is on, so Immersive Engineering and Immersive Petroleum are being treated as absent. Their recipes, multiblocks and placed blocks will not work. You said you can replace the needed recipes and NBT data yourself."); }
        immersiveEngineering = !disabled && ModList.get().isLoaded(IMMERSIVE_ENGINEERING);
        ieContent = immersiveEngineering || ModList.get().isLoaded(IMMERSIVE_FABRICATION);
        immersivePetroleum = immersiveEngineering && ModList.get().isLoaded(IMMERSIVE_PETROLEUM);
        initialized = true;
    }

    public static boolean disableImmersiveEngineering() { return earlyBoolean(ICLib.MODID + "-common.toml", DISABLE_IMMERSIVE_ENGINEERING, false); }

    public static boolean earlyBoolean(String fileName, String path, boolean fallback) {
        try {
            Config config = TomlFormat.instance().createParser().parse(FMLPaths.CONFIGDIR.get().resolve(fileName), FileNotFoundAction.READ_NOTHING);
            return config.getOrElse(path, fallback);
        }
        catch (ParsingException e) { return fallback; }
    }

    private static void ensureInitialized() { if (!initialized) { init(); } }

    public static boolean immersiveEngineering() { ensureInitialized(); return immersiveEngineering; }

    public static boolean ieContent() { ensureInitialized(); return ieContent; }

    public static boolean immersivePetroleum() { ensureInitialized(); return immersivePetroleum; }

    public static boolean computerCraft() { return ModList.get().isLoaded(COMPUTERCRAFT); }

    public static boolean openComputers() { return ModList.get().isLoaded(OPENCOMPUTERS); }

    public static ResourceLocation ie(String path) { return ResourceLocation.fromNamespaceAndPath(IMMERSIVE_ENGINEERING, path); }

    public static ResourceLocation ifab(String path) { return ResourceLocation.fromNamespaceAndPath(IMMERSIVE_FABRICATION, path); }
}
