package com.immersiveconvergence.common.util.compat.jei;

import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public abstract class ICRecipeCategory<T, W extends IRecipeWrapper> implements IRecipeCategory<W>, IRecipeWrapperFactory<T> {
    private final String uid;
    private final String title;
    private final IDrawable background;
    private final Class<T> recipeClass;
    private final ItemStack[] catalysts;

    protected ICRecipeCategory(String uniqueName, String localKey, IDrawable background, Class<T> recipeClass, ItemStack... catalysts) {
        this.uid = "ic." + uniqueName;
        this.title = I18n.format(localKey);
        this.background = background;
        this.recipeClass = recipeClass;
        this.catalysts = catalysts;
    }

    public void register(IModRegistry registry, List<?> recipes) {
        for (ItemStack stack : catalysts) { registry.addRecipeCatalyst(stack, uid); }
        registry.handleRecipes(recipeClass, this, uid);
        registry.addRecipes(recipes, uid);
    }

    @Override @Nonnull public String getUid() { return uid; }

    @Override @Nonnull public String getTitle() { return title; }

    @Override @Nonnull public String getModName() { return "Immersive Convergence"; }

    @Override @Nonnull public IDrawable getBackground() { return background; }

    @Override public void drawExtras(@Nonnull Minecraft minecraft) { }

    @Override @Nonnull public List<String> getTooltipStrings(int mouseX, int mouseY) { return Collections.emptyList(); }
}
