package com.immersiveconvergence.api.crafting.builders;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

@SuppressWarnings("unused") public class ICCrusherRecipeBuilder extends ICIERecipeBuilder {
    private ICCrusherRecipeBuilder() { super("crusher"); }

    public static ICCrusherRecipeBuilder builder() { return new ICCrusherRecipeBuilder(); }

    public ICCrusherRecipeBuilder input(ItemLike item) {
        json.add("input", ingredient(Ingredient.of(item)));
        return this;
    }

    public ICCrusherRecipeBuilder input(TagKey<Item> tag) {
        json.add("input", sized(tag, 1));
        return this;
    }

    public ICCrusherRecipeBuilder output(TagKey<Item> tag, int count) {
        json.add("result", sized(tag, count));
        return this;
    }

    public ICCrusherRecipeBuilder addSecondary(TagKey<Item> tag, float chance) {
        array("secondaries").add(chance(sized(tag, 1), chance));
        return this;
    }

    public ICCrusherRecipeBuilder setEnergy(int energy) {
        json.addProperty("energy", energy);
        return this;
    }
}
