package com.immersiveconvergence.common.util.compat.groovyscript;

import com.immersiveconvergence.api.crafting.ICIngredientStack;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

@SuppressWarnings("unused")
public abstract class ICFluidRecipeBuilder<T> extends AbstractRecipeBuilder<T> {

    protected int time;
    protected int energy;

    public ICFluidRecipeBuilder<T> time(int time) { this.time = time; return this; }

    public ICFluidRecipeBuilder<T> energy(int energy) { this.energy = energy; return this; }

    protected FluidStack fluidIn(int index) { return index < fluidInput.size() ? fluidInput.get(index) : null; }

    protected FluidStack fluidOut(int index) { return index < fluidOutput.size() ? fluidOutput.get(index) : null; }

    protected void requireTime(GroovyLog.Msg msg) {
        if (time < 1) { msg.add("the recipe needs a time of at least 1 tick, got " + time); }
    }

    protected void requireEnergy(GroovyLog.Msg msg) {
        if (energy < 0) { msg.add("the recipe cannot use negative energy, got " + energy); }
    }

    protected static Object toRecipeInput(IIngredient ingredient) {
        if (ingredient == null) { return null; }
        if (ingredient instanceof OreDictIngredient) {
            OreDictIngredient ore = (OreDictIngredient) ingredient;
            return ore.getAmount() > 1 ? new ICIngredientStack(ore.getOreDict(), ore.getAmount()) : ore.getOreDict();
        }
        ItemStack[] stacks = ingredient.getMatchingStacks();
        return stacks.length > 0 ? stacks[0] : null;
    }

    protected Object itemIn(int index) { return index < input.size() ? toRecipeInput(input.get(index)) : null; }

    protected ItemStack itemOut(int index) { return index < output.size() ? output.get(index) : ItemStack.EMPTY; }
}
