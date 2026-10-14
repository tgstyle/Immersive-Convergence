package com.immersiveconvergence.api.crafting.builders;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

@SuppressWarnings("unused") public class ICMetalPressRecipeBuilder extends ICIERecipeBuilder {
    private ICMetalPressRecipeBuilder() { super("metal_press"); }

    public static ICMetalPressRecipeBuilder builder() { return new ICMetalPressRecipeBuilder(); }

    public ICMetalPressRecipeBuilder input(TagKey<Item> tag) {
        json.add("input", sized(tag, 1));
        return this;
    }

    public ICMetalPressRecipeBuilder mold(ResourceLocation mold) {
        json.addProperty("mold", mold.toString());
        return this;
    }

    public ICMetalPressRecipeBuilder output(ItemLike item, int count) {
        json.add("result", stack(item, count));
        return this;
    }

    public ICMetalPressRecipeBuilder output(TagKey<Item> tag, int count) {
        json.add("result", sized(tag, count));
        return this;
    }

    public ICMetalPressRecipeBuilder setEnergy(int energy) {
        json.addProperty("energy", energy);
        return this;
    }
}
