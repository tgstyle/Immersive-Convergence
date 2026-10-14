package com.immersiveconvergence.api.crafting;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IESerializableRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.util.Lazy;

@SuppressWarnings("unused") public abstract class ICSerializableRecipe extends IESerializableRecipe {
    protected <T extends Recipe<?>> ICSerializableRecipe(Lazy<ItemStack> outputDummy, ICRecipeType<T> type, ResourceLocation id) { super(outputDummy, type.ie(), id); }

    protected abstract ICRecipeSerializer<?> getICSerializer();

    @Override protected final IERecipeSerializer<?> getIESerializer() { return getICSerializer(); }
}
