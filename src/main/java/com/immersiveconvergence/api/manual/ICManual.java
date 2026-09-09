package com.immersiveconvergence.api.manual;

import com.immersiveconvergence.api.multiblock.ICMultiblock;
import com.immersiveconvergence.common.manual.IEManualBridge;

import net.minecraftforge.fml.common.Loader;

@SuppressWarnings("unused")
public class ICManual {
    public static final String CAT_GENERAL = "general";
    public static final String CAT_CONSTRUCTION = "construction";
    public static final String CAT_ENERGY = "energy";
    public static final String CAT_MACHINES = "machines";
    public static final String CAT_TOOLS = "tools";

    public static boolean available() { return Loader.isModLoaded("immersiveengineering"); }

    public static void addEntry(String name, String category, ICManualPage... pages) {
        if (available()) { IEManualBridge.addEntry(name, category, pages); }
    }

    public static ICManualPage text(String key) { return ICManualPage.text(key); }

    public static ICManualPage multiblock(String key, ICMultiblock multiblock) { return ICManualPage.multiblock(key, multiblock); }

    public static ICManualPage crafting(String key, Object... stacks) { return ICManualPage.crafting(key, stacks); }

    public static ICManualPage image(String key, String... images) { return ICManualPage.image(key, images); }
}
