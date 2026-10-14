package com.immersiveconvergence.api.crafting.builders;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

@SuppressWarnings("unused") public class ICAlloyRecipeBuilder extends ICIERecipeBuilder {
    private int inputs;

    private ICAlloyRecipeBuilder() { super("alloy"); }

    public static ICAlloyRecipeBuilder builder() { return new ICAlloyRecipeBuilder(); }

    public ICAlloyRecipeBuilder input(TagKey<Item> tag, int count) {
        json.add("input" + inputs++, sized(tag, count));
        return this;
    }

    public ICAlloyRecipeBuilder output(TagKey<Item> tag, int count) {
        json.add("result", sized(tag, count));
        return this;
    }
}
