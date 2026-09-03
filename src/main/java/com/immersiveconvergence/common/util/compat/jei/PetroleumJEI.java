package com.immersiveconvergence.common.util.compat.jei;

import flaxbeard.immersivepetroleum.api.crafting.DistillationRecipe;
import flaxbeard.immersivepetroleum.api.crafting.PumpjackHandler;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;

import java.util.ArrayList;

public final class PetroleumJEI {
    public static final String DISTILLATION_TOWER = "ic.distillationTower";
    public static final String RESERVOIR = "ic.reservoir";
    private static DistillationTowerCategory distillationTower;
    private static ReservoirCategory reservoir;

    private PetroleumJEI() {}

    public static void registerCategories(IRecipeCategoryRegistration registry) {
        distillationTower = new DistillationTowerCategory(registry.getJeiHelpers().getGuiHelper());
        reservoir = new ReservoirCategory(registry.getJeiHelpers().getGuiHelper());
        registry.addRecipeCategories(distillationTower, reservoir);
    }

    public static void register(IModRegistry registry) {
        distillationTower.register(registry, new ArrayList<>(DistillationRecipe.recipeList));
        reservoir.register(registry, new ArrayList<>(PumpjackHandler.reservoirList.keySet()));
    }
}
