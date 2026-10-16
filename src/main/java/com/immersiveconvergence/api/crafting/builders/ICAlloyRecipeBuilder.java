package com.immersiveconvergence.api.crafting.builders;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ICAlloyRecipeBuilder extends ICIERecipeBuilder<ICAlloyRecipeBuilder> {
    private int inputs;

    private ICAlloyRecipeBuilder() { super("alloy"); }

    public static ICAlloyRecipeBuilder builder() { return new ICAlloyRecipeBuilder(); }

    @Override public ICAlloyRecipeBuilder input(TagKey<Item> tag, int count) {
        json.add("input" + inputs++, sized(tag, count));
        return this;
    }
}
