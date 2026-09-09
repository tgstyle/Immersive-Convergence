package com.immersiveconvergence.common.util.compat.groovyscript;

import com.immersiveconvergence.api.crafting.ICRecipeBase;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ICFluidRecipeRegistry<T extends ICRecipeBase> extends VirtualizedRegistry<T> {

    private final Supplier<Collection<T>> recipes;
    private final BooleanSupplier enabled;
    private final Consumer<T> adder;
    private final Consumer<T> remover;

    public ICFluidRecipeRegistry(String name, Supplier<Collection<T>> recipes, BooleanSupplier enabled, Consumer<T> adder, Consumer<T> remover) {
        super(Collections.singletonList(name));
        this.recipes = recipes;
        this.enabled = enabled;
        this.adder = adder;
        this.remover = remover;
    }

    public Collection<T> getRecipes() { return recipes.get(); }

    @Override public boolean isEnabled() { return enabled.getAsBoolean(); }

    @Override
    public void onReload() {
        removeScripted().forEach(remover);
        restoreFromBackup().forEach(adder);
    }

    public boolean add(T recipe) {
        if (recipe == null) { return false; }
        adder.accept(recipe);
        addScripted(recipe);
        return true;
    }

    public boolean remove(T recipe) {
        if (recipe == null) { return false; }
        remover.accept(recipe);
        addBackup(recipe);
        return true;
    }

    public void removeAll() {
        for (T recipe : new ArrayList<>(getRecipes())) { remove(recipe); }
    }

    public SimpleObjectStream<T> streamRecipes() {
        return new SimpleObjectStream<>(getRecipes()).setRemover(this::remove);
    }

    public void removeByFluidInput(IIngredient input) {
        removeMatching(recipe -> matchesFluid(input, recipe.getFluidInputs()));
    }

    public void removeByFluidOutput(IIngredient output) {
        removeMatching(recipe -> matchesFluid(output, recipe.getFluidOutputs()));
    }

    public void removeByItemOutput(IIngredient output) {
        removeMatching(recipe -> matchesItem(output, recipe.getItemOutputs()));
    }

    private void removeMatching(Predicate<T> test) {
        for (T recipe : new ArrayList<>(getRecipes())) {
            if (recipe != null && test.test(recipe)) { remove(recipe); }
        }
    }

    public static FluidStack fluidInputAt(ICRecipeBase recipe, int index) {
        List<FluidStack> inputs = recipe == null ? null : recipe.getFluidInputs();
        return inputs != null && index < inputs.size() ? inputs.get(index) : null;
    }

    private static boolean matchesFluid(IIngredient ingredient, List<FluidStack> stacks) {
        if (ingredient == null || stacks == null) { return false; }
        for (FluidStack stack : stacks) {
            if (stack != null && ingredient.test(stack)) { return true; }
        }
        return false;
    }

    private static boolean matchesItem(IIngredient ingredient, List<ItemStack> stacks) {
        if (ingredient == null || stacks == null) { return false; }
        for (ItemStack stack : stacks) {
            if (stack != null && !stack.isEmpty() && ingredient.test(stack)) { return true; }
        }
        return false;
    }
}
