package com.immersiveconvergence.common.util.compat.jei;

import com.immersiveconvergence.api.jei.MultiblockIngredient;
import com.immersiveconvergence.core.ICClientConfig;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;

@JEIPlugin
public class ICJEIPlugin implements IModPlugin {

    @SuppressWarnings("deprecation")
    @Override public void registerIngredients(@Nonnull IModIngredientRegistration registry) {
        EngineeringMultiblockIngredients.register();
        Collection<MultiblockIngredient> listed = ICClientConfig.jei.showMultiblockItems ? MultiblockIngredient.list : Collections.emptyList();
        registry.register(MultiblockIngredient.class, listed, new MultiblockIngredientHelper(), MultiblockIngredientRenderer.INSTANCE);
    }

    @Override public void registerCategories(@Nonnull IRecipeCategoryRegistration registry) { if (Loader.isModLoaded("immersivepetroleum")) { PetroleumJEI.registerCategories(registry); } }

    @Override public void register(@Nonnull IModRegistry registry) {
        if (Loader.isModLoaded("immersivepetroleum")) { PetroleumJEI.register(registry); }
        for (MultiblockIngredient ingredient : MultiblockIngredient.list) {
            if (ingredient.catalystUids.length > 0) { registry.addRecipeCatalyst(ingredient, ingredient.catalystUids); }
        }
    }
}
