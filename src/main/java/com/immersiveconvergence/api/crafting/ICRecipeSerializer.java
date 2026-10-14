package com.immersiveconvergence.api.crafting;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import net.minecraft.world.item.crafting.Recipe;

public abstract class ICRecipeSerializer<R extends Recipe<?>> extends IERecipeSerializer<R> {}
