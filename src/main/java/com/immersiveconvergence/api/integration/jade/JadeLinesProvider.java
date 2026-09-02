package com.immersiveconvergence.api.integration.jade;

import com.immersiveconvergence.api.integration.DisplayContexts;
import com.immersiveconvergence.api.integration.DisplayLines;
import com.immersiveconvergence.api.multiblock.IDisplayContext;
import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum JadeLinesProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    private static final String TAG = "ICLines";

    @Override public ResourceLocation getUid() { return ICLib.rl("lines"); }

    @Override public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        IDisplayContext context = DisplayContexts.of(accessor.getBlockEntity());
        if (context == null) { return; }
        DisplayLines lines = new DisplayLines();
        context.addDisplayLines(accessor.getLevel(), lines);
        if (!lines.lines().isEmpty()) { data.put(TAG, lines.write()); }
    }

    @Override public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (!data.contains(TAG, Tag.TAG_LIST)) { return; }
        for (DisplayLines.Line line : DisplayLines.read(data.getList(TAG, Tag.TAG_COMPOUND)).lines()) { tooltip.add(DisplayLines.describe(line)); }
    }
}
