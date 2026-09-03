package com.immersiveconvergence.common.util.compat.jei;

import com.immersiveconvergence.api.jei.MultiblockIngredient;

import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class MultiblockIngredientRenderer implements IIngredientRenderer<MultiblockIngredient> {
    public static final MultiblockIngredientRenderer INSTANCE = new MultiblockIngredientRenderer();

    @Override public void render(@Nonnull Minecraft minecraft, int x, int y, @Nullable MultiblockIngredient ingredient) {
        if (ingredient == null) { return; }
        if (ingredient.modelRender != null) {
            ingredient.modelRender.render(x, y);
            return;
        }
        RenderHelper.enableGUIStandardItemLighting();
        minecraft.getRenderItem().renderItemAndEffectIntoGUI(null, ingredient.renderStack, x, y);
        GlStateManager.disableBlend();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.color(1, 1, 1, 1);
    }

    @Override @Nonnull public List<String> getTooltip(@Nonnull Minecraft minecraft, @Nonnull MultiblockIngredient ingredient, @Nonnull ITooltipFlag flag) {
        return Arrays.asList(ingredient.renderStack.getDisplayName(), I18n.format("gui.immersiveconvergence.multiblock_jei_tooltip"));
    }
}
