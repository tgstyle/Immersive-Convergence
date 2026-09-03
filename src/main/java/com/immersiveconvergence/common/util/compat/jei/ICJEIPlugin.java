package com.immersiveconvergence.common.util.compat.jei;

import com.immersiveconvergence.api.jei.MultiblockIngredient;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nonnull;

@JEIPlugin
public class ICJEIPlugin implements IModPlugin {

    @SuppressWarnings("deprecation")
    @Override public void registerIngredients(@Nonnull IModIngredientRegistration registry) {
        EngineeringMultiblockIngredients.register();
        registry.register(MultiblockIngredient.class, MultiblockIngredient.list, new MultiblockIngredientHelper(), MultiblockIngredientRenderer.INSTANCE);
    }

    @Override public void registerCategories(@Nonnull IRecipeCategoryRegistration registry) { if (Loader.isModLoaded("immersivepetroleum")) { PetroleumJEI.registerCategories(registry); } }

    @Override public void register(@Nonnull IModRegistry registry) {
        if (Loader.isModLoaded("immersivepetroleum")) { PetroleumJEI.register(registry); }
        for (MultiblockIngredient ingredient : MultiblockIngredient.list) {
            if (ingredient.catalystUids.length > 0) { registry.addRecipeCatalyst(ingredient, ingredient.catalystUids); }
        }
    }
}
