package com.immersiveconvergence.api.crafting.builders;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class ICCrusherRecipeBuilder extends ICIERecipeBuilder<ICCrusherRecipeBuilder> {
    private ICCrusherRecipeBuilder() { super("crusher"); }

    public static ICCrusherRecipeBuilder builder() { return new ICCrusherRecipeBuilder(); }

    public ICCrusherRecipeBuilder input(ItemLike item) {
        json.add("input", ingredient(Ingredient.of(item)));
        return this;
    }
}
