package com.immersiveconvergence.api.crafting.builders;

import com.immersiveconvergence.api.crafting.ICFluidTagInput;
import com.immersiveconvergence.api.util.ICUtils;

import com.google.gson.JsonArray;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class ICMixerRecipeBuilder extends ICIERecipeBuilder<ICMixerRecipeBuilder> {
    private ICMixerRecipeBuilder() { super("mixer"); }

    public static ICMixerRecipeBuilder builder() { return new ICMixerRecipeBuilder(); }

    public ICMixerRecipeBuilder output(Fluid fluid, int amount) {
        json.add("result", ICUtils.jsonSerializeFluidStack(new FluidStack(fluid, amount)));
        return this;
    }

    public ICMixerRecipeBuilder fluidInput(TagKey<Fluid> tag, int amount) {
        json.add("fluid", new ICFluidTagInput(tag, amount).serialize());
        return this;
    }

    public ICMixerRecipeBuilder input(TagKey<Item> tag, int count) {
        if (!json.has("inputs")) { json.add("inputs", new JsonArray()); }
        json.getAsJsonArray("inputs").add(sized(tag, count));
        return this;
    }
}
