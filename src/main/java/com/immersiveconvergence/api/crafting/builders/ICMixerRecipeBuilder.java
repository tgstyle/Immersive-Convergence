package com.immersiveconvergence.api.crafting.builders;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class ICMixerRecipeBuilder extends ICIERecipeBuilder<ICMixerRecipeBuilder> {
    private ICMixerRecipeBuilder() { super("mixer"); }

    public static ICMixerRecipeBuilder builder() { return new ICMixerRecipeBuilder(); }

    public ICMixerRecipeBuilder output(Fluid fluid, int amount) { return result(encode(FluidStack.CODEC, new FluidStack(fluid, amount))); }

    public ICMixerRecipeBuilder fluidInput(TagKey<Fluid> tag, int amount) {
        json.add("fluid", encode(SizedFluidIngredient.FLAT_CODEC, SizedFluidIngredient.of(tag, amount)));
        return this;
    }

    @Override public ICMixerRecipeBuilder input(TagKey<Item> tag, int count) {
        array("inputs").add(sized(tag, count));
        return this;
    }
}
