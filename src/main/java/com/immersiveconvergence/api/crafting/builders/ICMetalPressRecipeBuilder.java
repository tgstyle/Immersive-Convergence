package com.immersiveconvergence.api.crafting.builders;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

public class ICMetalPressRecipeBuilder extends ICIERecipeBuilder<ICMetalPressRecipeBuilder> {
    private ICMetalPressRecipeBuilder() { super("metal_press"); }

    public static ICMetalPressRecipeBuilder builder() { return new ICMetalPressRecipeBuilder(); }

    public ICMetalPressRecipeBuilder mold(ResourceLocation mold) {
        json.addProperty("mold", mold.toString());
        return this;
    }

    public ICMetalPressRecipeBuilder result(ItemLike item, int count) { return result(stack(item, count)); }
}
