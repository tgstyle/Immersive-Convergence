package com.immersiveconvergence.api.integration.jei;

import com.immersiveconvergence.core.lib.ICLib;

import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockItem;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@JeiPlugin
public class ICJEIPlugin implements IModPlugin {
    private static final ResourceLocation ID = ICLib.rl("main");

    @Override @Nonnull public ResourceLocation getPluginUid() { return ID; }

    @Override public void registerExtraIngredients(@Nonnull IExtraIngredientRegistration registration) {
        List<ItemStack> multiblocks = new ArrayList<>();
        for (Item item : ForgeRegistries.ITEMS) {
            if (item instanceof MultiblockItem) { multiblocks.add(item.getDefaultInstance()); }
        }
        registration.addExtraItemStacks(multiblocks);
    }
}
