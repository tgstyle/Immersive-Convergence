package com.immersiveconvergence.api.crafting.builders;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ICArcFurnaceRecipeBuilder extends ICIERecipeBuilder<ICArcFurnaceRecipeBuilder> {
    private ICArcFurnaceRecipeBuilder() {
        super("arc_furnace");
        array("additives");
    }

    public static ICArcFurnaceRecipeBuilder builder() { return new ICArcFurnaceRecipeBuilder(); }

    public ICArcFurnaceRecipeBuilder additive(TagKey<Item> tag) { return additive(tag, 1); }

    public ICArcFurnaceRecipeBuilder additive(TagKey<Item> tag, int count) {
        array("additives").add(sized(tag, count));
        return this;
    }

    public ICArcFurnaceRecipeBuilder output(TagKey<Item> tag, int count) {
        array("results").add(sized(tag, count));
        return this;
    }

    public ICArcFurnaceRecipeBuilder slag(TagKey<Item> tag, int count) {
        json.add("slag", sized(tag, count));
        return this;
    }
}
