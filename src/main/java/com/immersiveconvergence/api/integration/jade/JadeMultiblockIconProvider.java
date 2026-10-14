package com.immersiveconvergence.api.integration.jade;

import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

public enum JadeMultiblockIconProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override public ResourceLocation getUid() { return ICLib.rl("multiblock_icon"); }

    @Override public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {}

    @Override public IElement getIcon(BlockAccessor accessor, IPluginConfig config, IElement currentIcon) { return IElementHelper.get().item(new ItemStack(accessor.getBlock())); }
}
