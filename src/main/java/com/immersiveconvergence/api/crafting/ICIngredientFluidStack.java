package com.immersiveconvergence.api.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class ICIngredientFluidStack extends Ingredient {
    private final FluidStack fluid;
    private ItemStack[] cachedStacks;

    public ICIngredientFluidStack(FluidStack fluid) {
        super(0);
        this.fluid = fluid;
    }

    public ICIngredientFluidStack(Fluid fluid, int amount) { this(new FluidStack(fluid, amount)); }

    public FluidStack getFluid() { return fluid; }

    @Override @Nonnull public ItemStack[] getMatchingStacks() {
        if (cachedStacks == null) { cachedStacks = new ItemStack[]{FluidUtil.getFilledBucket(fluid)}; }
        return this.cachedStacks;
    }

    @Override public boolean apply(@Nullable ItemStack stack) {
        if (stack == null) { return false; }
        FluidStack contained = FluidUtil.getFluidContained(stack);
        return contained == null && this.fluid == null || contained != null && contained.containsFluid(fluid);
    }
}
