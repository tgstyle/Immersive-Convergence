package com.immersiveconvergence.api.crafting.builders;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

@SuppressWarnings("unused") public class ICMixerRecipeBuilder extends ICIERecipeBuilder {
    private ICMixerRecipeBuilder() { super("mixer"); }

    public static ICMixerRecipeBuilder builder() { return new ICMixerRecipeBuilder(); }

    public ICMixerRecipeBuilder output(Fluid fluid, int amount) {
        JsonObject result = new JsonObject();
        result.addProperty("id", BuiltInRegistries.FLUID.getKey(fluid).toString());
        result.addProperty("amount", amount);
        json.add("result", result);
        return this;
    }

    public ICMixerRecipeBuilder fluidInput(TagKey<Fluid> tag, int amount) {
        JsonObject fluid = new JsonObject();
        fluid.addProperty("tag", tag.location().toString());
        fluid.addProperty("amount", amount);
        json.add("fluid", fluid);
        return this;
    }

    public ICMixerRecipeBuilder input(TagKey<Item> tag, int count) {
        array("inputs").add(sized(tag, count));
        return this;
    }

    public ICMixerRecipeBuilder setEnergy(int energy) {
        json.addProperty("energy", energy);
        return this;
    }
}
