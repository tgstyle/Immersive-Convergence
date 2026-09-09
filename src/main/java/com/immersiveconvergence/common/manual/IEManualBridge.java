package com.immersiveconvergence.common.manual;

import com.immersiveconvergence.api.manual.ICManualPage;
import com.immersiveconvergence.api.multiblock.ICMultiblock;
import com.immersiveconvergence.common.multiblock.IEMultiblockBridge;

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.immersiveengineering.api.ManualPageMultiblock;
import blusunrize.lib.manual.IManualPage;
import blusunrize.lib.manual.ManualPages;

import java.util.ArrayList;
import java.util.List;

public final class IEManualBridge {
    private IEManualBridge() {}

    public static void addEntry(String name, String category, ICManualPage... pages) {
        List<IManualPage> built = new ArrayList<>();
        for (ICManualPage page : pages) {
            IManualPage made = build(page);
            if (made != null) { built.add(made); }
        }
        ManualHelper.addEntry(name, category, built.toArray(new IManualPage[0]));
    }

    private static IManualPage build(ICManualPage page) {
        switch (page.kind()) {
            case TEXT: return new ManualPages.Text(ManualHelper.getManual(), page.key());
            case CRAFTING: return new ManualPages.Crafting(ManualHelper.getManual(), page.key(), page.arguments());
            case IMAGE: return new ManualPages.Image(ManualHelper.getManual(), page.key(), toStrings(page.arguments()));
            case MULTIBLOCK: {
                ICMultiblock multiblock = page.multiblock();
                return multiblock == null ? null : new ManualPageMultiblock(ManualHelper.getManual(), page.key(), IEMultiblockBridge.adapt(multiblock));
            }
            default: return null;
        }
    }

    private static String[] toStrings(Object[] arguments) {
        String[] images = new String[arguments.length];
        for (int i = 0; i < arguments.length; i++) { images[i] = String.valueOf(arguments[i]); }
        return images;
    }
}
