package com.immersiveconvergence.api.integration.jei;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.crafting.ICCokeOvenRecipe;
import com.immersiveconvergence.common.ie.IECokeOvenJEIBridge;

import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.world.level.Level;
import java.util.function.Predicate;

@SuppressWarnings("unused") public final class ICCokeOvenJEI {
    private ICCokeOvenJEI() {}

    public static void addRecipes(IRecipeRegistration registration, Level level, Predicate<ICCokeOvenRecipe> filter) {
        if (ICMods.immersiveEngineering()) { IECokeOvenJEIBridge.addRecipes(registration, level, filter); }
    }
}
