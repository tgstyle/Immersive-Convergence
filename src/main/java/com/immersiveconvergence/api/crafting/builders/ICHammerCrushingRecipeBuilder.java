package com.immersiveconvergence.api.crafting.builders;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

@SuppressWarnings("unused") public class ICHammerCrushingRecipeBuilder extends ICIERecipeBuilder {
    private ICHammerCrushingRecipeBuilder(TagKey<Item> input, TagKey<Item> output) {
        super("hammer_crushing");
        json.add("input", sized(input, 1));
        json.add("result", sized(output, 1));
    }

    public static ICHammerCrushingRecipeBuilder builder(TagKey<Item> input, TagKey<Item> output) { return new ICHammerCrushingRecipeBuilder(input, output); }
}
