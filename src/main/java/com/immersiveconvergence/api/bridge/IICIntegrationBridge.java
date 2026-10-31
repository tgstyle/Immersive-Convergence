package com.immersiveconvergence.api.bridge;

import com.immersiveconvergence.api.crafting.ICCokeOvenRecipe;
import com.immersiveconvergence.api.crafting.ICRecipeType;
import com.immersiveconvergence.api.manual.ICManualCategory;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public interface IICIntegrationBridge {
    void addRenderCacheClearer(@Nonnull Runnable clearer);

    boolean isWireCoil(@Nonnull ItemStack stack);

    void addManualEntry(@Nonnull ICManualCategory category, @Nonnull ResourceLocation file);

    void addManualConfigGetter(@Nonnull Function<String, Object> getter);

    void resetManual();

    void reloadManual();

    @Nonnull List<ICCokeOvenRecipe> cokeOvenRecipes(@Nonnull Level level);

    @Nullable ICCokeOvenRecipe cokeOvenRecipeById(@Nonnull Level level, @Nonnull ResourceLocation id);

    @Nonnull ICRecipeType<?> cokeOvenRecipeType();

    boolean isFluidPipe(@Nullable BlockEntity tile);

    void refreshFluidPipe(@Nonnull BlockEntity tile, @Nonnull Direction side);

    void markPressurized(@Nonnull FluidStack stack);

    @Nonnull ItemStack preferredStack(@Nonnull ItemStack[] stacks);
}
