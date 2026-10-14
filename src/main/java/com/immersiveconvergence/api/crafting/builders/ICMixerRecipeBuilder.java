package com.immersiveconvergence.api.crafting.builders;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

@SuppressWarnings("unused") public class ICMixerRecipeBuilder extends ICIERecipeBuilder {
    private ICMixerRecipeBuilder() { super("mixer"); }

    public static ICMixerRecipeBuilder builder(Fluid fluid, int amount) {
        ICMixerRecipeBuilder builder = new ICMixerRecipeBuilder();
        JsonObject result = new JsonObject();
        result.addProperty("fluid", Objects.requireNonNull(ForgeRegistries.FLUIDS.getKey(fluid)).toString());
        result.addProperty("amount", amount);
        builder.json.add("result", result);
        return builder;
    }

    public ICMixerRecipeBuilder addFluidTag(TagKey<Fluid> tag, int amount) {
        JsonObject fluid = new JsonObject();
        fluid.addProperty("tag", tag.location().toString());
        fluid.addProperty("amount", amount);
        json.add("fluid", fluid);
        return this;
    }

    public ICMixerRecipeBuilder addInput(TagKey<Item> tag, int count) {
        if (!json.has("inputs")) { json.add("inputs", new JsonArray()); }
        json.getAsJsonArray("inputs").add(sized(tag, count));
        return this;
    }

    public ICMixerRecipeBuilder setEnergy(int energy) {
        json.addProperty("energy", energy);
        return this;
    }
}
