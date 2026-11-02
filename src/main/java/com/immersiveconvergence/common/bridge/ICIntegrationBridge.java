package com.immersiveconvergence.common.bridge;

import com.immersiveconvergence.api.bridge.IICIntegrationBridge;
import com.immersiveconvergence.api.crafting.ICCokeOvenRecipe;
import com.immersiveconvergence.api.crafting.ICRecipeType;
import com.immersiveconvergence.api.manual.ICManualCategory;
import com.immersiveconvergence.core.integration.immersiveengineering.IECokeOvenBridge;
import com.immersiveconvergence.core.integration.immersiveengineering.IEFluidPipeBridge;
import com.immersiveconvergence.core.integration.immersiveengineering.IEManualBridge;
import com.immersiveconvergence.core.integration.immersiveengineering.IERecipeBridge;
import com.immersiveconvergence.core.integration.immersiveengineering.IEWireBridge;

import blusunrize.immersiveengineering.api.IEApi;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ICIntegrationBridge implements IICIntegrationBridge {
    @Override public void addRenderCacheClearer(@Nonnull Runnable clearer) { IEApi.renderCacheClearers.add(clearer); }

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

    @Override @Nonnull public ItemStack preferredStack(@Nonnull ItemStack[] stacks) { return IERecipeBridge.preferredStack(stacks); }
}
