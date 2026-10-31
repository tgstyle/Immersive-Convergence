package com.immersiveconvergence.common.bridge;

import com.immersiveconvergence.api.bridge.IICIntegrationBridge;
import com.immersiveconvergence.api.crafting.ICCokeOvenRecipe;
import com.immersiveconvergence.api.crafting.ICRecipeType;
import com.immersiveconvergence.api.manual.ICManualCategory;
import com.immersiveconvergence.common.ie.IECokeOvenBridge;
import com.immersiveconvergence.common.ie.IEFluidPipeBridge;
import com.immersiveconvergence.common.ie.IEManualBridge;
import com.immersiveconvergence.common.ie.IERecipeBridge;
import com.immersiveconvergence.common.ie.IERenderBridge;
import com.immersiveconvergence.common.ie.IEWireBridge;

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

public final class ICIntegrationBridge implements IICIntegrationBridge {
    @Override public void addRenderCacheClearer(@Nonnull Runnable clearer) { IERenderBridge.addRenderCacheClearer(clearer); }

    @Override public boolean isWireCoil(@Nonnull ItemStack stack) { return IEWireBridge.isWireCoil(stack); }

    @Override public void addManualEntry(@Nonnull ICManualCategory category, @Nonnull ResourceLocation file) { IEManualBridge.addEntry(category, file); }

    @Override public void addManualConfigGetter(@Nonnull Function<String, Object> getter) { IEManualBridge.addConfigGetter(getter); }

    @Override public void resetManual() { IEManualBridge.reset(); }

    @Override public void reloadManual() { IEManualBridge.reload(); }

    @Override @Nonnull public List<ICCokeOvenRecipe> cokeOvenRecipes(@Nonnull Level level) { return IECokeOvenBridge.recipes(level); }

    @Override @Nullable public ICCokeOvenRecipe cokeOvenRecipeById(@Nonnull Level level, @Nonnull ResourceLocation id) { return IECokeOvenBridge.getById(level, id); }

    @Override @Nonnull public ICRecipeType<?> cokeOvenRecipeType() { return IECokeOvenBridge.recipeType(); }

    @Override public boolean isFluidPipe(@Nullable BlockEntity tile) { return IEFluidPipeBridge.isPipe(tile); }

    @Override public void refreshFluidPipe(@Nonnull BlockEntity tile, @Nonnull Direction side) { IEFluidPipeBridge.refreshConnection(tile, side); }

    @Override public void markPressurized(@Nonnull FluidStack stack) { IEFluidPipeBridge.markPressurized(stack); }

    @Override @Nonnull public ItemStack preferredStack(@Nonnull ItemStack[] stacks) { return IERecipeBridge.preferredStack(stacks); }
}
