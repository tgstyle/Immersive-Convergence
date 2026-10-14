package com.immersiveconvergence.api.crafting.builders;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

@SuppressWarnings("unused") public class ICArcFurnaceRecipeBuilder extends ICIERecipeBuilder {
    private ICArcFurnaceRecipeBuilder() {
        super("arc_furnace");
        array("additives");
    }

    public static ICArcFurnaceRecipeBuilder builder() { return new ICArcFurnaceRecipeBuilder(); }

    public ICArcFurnaceRecipeBuilder input(TagKey<Item> tag) { return input(tag, 1); }

    public ICArcFurnaceRecipeBuilder input(TagKey<Item> tag, int count) {
        json.add("input", sized(tag, count));
        return this;
    }

    public ICArcFurnaceRecipeBuilder additive(TagKey<Item> tag) { return additive(tag, 1); }

    public ICArcFurnaceRecipeBuilder additive(TagKey<Item> tag, int count) {
        array("additives").add(sized(tag, count));
        return this;
    }

    public ICArcFurnaceRecipeBuilder output(TagKey<Item> tag, int count) {
        array("results").add(sized(tag, count));
        return this;
    }

    public ICArcFurnaceRecipeBuilder secondary(TagKey<Item> tag, float chance) {
        array("secondaries").add(chance(sized(tag, 1), chance));
        return this;
    }

    public ICArcFurnaceRecipeBuilder slag(TagKey<Item> tag, int count) {
        json.add("slag", sized(tag, count));
        return this;
    }

    public ICArcFurnaceRecipeBuilder setTime(int time) {
        json.addProperty("time", time);
        return this;
    }

    public ICArcFurnaceRecipeBuilder setEnergy(int energy) {
        json.addProperty("energy", energy);
        return this;
    }
}
