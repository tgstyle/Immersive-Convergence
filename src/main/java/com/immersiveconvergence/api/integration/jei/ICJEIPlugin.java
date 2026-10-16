package com.immersiveconvergence.api.integration.jei;

import com.immersiveconvergence.core.lib.ICLib;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import net.minecraft.resources.ResourceLocation;
import javax.annotation.Nonnull;

@JeiPlugin public class ICJEIPlugin implements IModPlugin {
    private static final ResourceLocation ID = ICLib.rl("main");

    @Override @Nonnull public ResourceLocation getPluginUid() { return ID; }
}
