package com.immersiveconvergence.api.crafting.builders;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ICHammerCrushingRecipeBuilder extends ICIERecipeBuilder<ICHammerCrushingRecipeBuilder> {
    private ICHammerCrushingRecipeBuilder() { super("hammer_crushing"); }

    public static ICHammerCrushingRecipeBuilder builder(TagKey<Item> input, TagKey<Item> output) { return new ICHammerCrushingRecipeBuilder().input(input, 1).result(output, 1); }
}
