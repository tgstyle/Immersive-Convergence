package com.immersiveconvergence.common.ie;

import com.immersiveconvergence.api.manual.ICManualCategory;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.lib.manual.ManualEntry;
import blusunrize.lib.manual.ManualInstance;
import blusunrize.lib.manual.Tree.InnerNode;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public final class IEManualBridge {
    private IEManualBridge() {}

    public static void addEntry(ICManualCategory category, ResourceLocation file) {
        ManualInstance manual = ManualHelper.getManual();
        ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(manual);
        builder.readFromFile(file);
        manual.addEntry(node(manual, category), builder.create());
    }

    public static void addConfigGetter(Function<String, Object> getter) { ManualHelper.addConfigGetter(getter); }

    public static void reset() {
        ManualInstance manual = ManualHelper.getManual();
        if (manual != null) { manual.reset(); }
    }

    public static void reload() { ImmersiveEngineering.proxy.resetManual(); }

    private static InnerNode<ResourceLocation, ManualEntry> node(ManualInstance manual, ICManualCategory category) {
        InnerNode<ResourceLocation, ManualEntry> parent = category.parent() == null ? manual.getRoot() : node(manual, category.parent());
        return parent.getOrCreateSubnode(category.id(), category.priority());
    }
}
