package com.immersiveconvergence.api.manual;

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.immersiveengineering.api.ManualPageMultiblock;
import blusunrize.immersiveengineering.api.MultiblockHandler;
import blusunrize.lib.manual.IManualPage;
import blusunrize.lib.manual.ManualPages;

@SuppressWarnings("unused")
public class ICManual {
    public static final String CAT_GENERAL = ManualHelper.CAT_GENERAL;
    public static final String CAT_CONSTRUCTION = ManualHelper.CAT_CONSTRUCTION;
    public static final String CAT_ENERGY = ManualHelper.CAT_ENERGY;
    public static final String CAT_MACHINES = ManualHelper.CAT_MACHINES;
    public static final String CAT_TOOLS = ManualHelper.CAT_TOOLS;

    public static void addEntry(String name, String category, IManualPage... pages) { ManualHelper.addEntry(name, category, pages); }

    public static IManualPage text(String key) { return new ManualPages.Text(ManualHelper.getManual(), key); }

    public static IManualPage multiblock(String key, MultiblockHandler.IMultiblock multiblock) { return new ManualPageMultiblock(ManualHelper.getManual(), key, multiblock); }

    public static IManualPage crafting(String key, Object... stacks) { return new ManualPages.Crafting(ManualHelper.getManual(), key, stacks); }

    public static IManualPage image(String key, String... images) { return new ManualPages.Image(ManualHelper.getManual(), key, images); }
}
