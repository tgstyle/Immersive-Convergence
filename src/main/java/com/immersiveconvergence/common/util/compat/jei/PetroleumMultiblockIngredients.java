package com.immersiveconvergence.common.util.compat.jei;

import com.immersiveconvergence.api.jei.MultiblockIngredient;
import com.immersiveconvergence.client.render.ip.TileRenderIPPumpjack;

import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.blocks.metal.BlockTypes_IPMetalMultiblock;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

public final class PetroleumMultiblockIngredients {
    private static final TileRenderIPPumpjack PUMPJACK = new TileRenderIPPumpjack();

    private PetroleumMultiblockIngredients() {}

    public static void register() {
        new MultiblockIngredient(new ItemStack(IPContent.blockMetalMultiblock, 1, BlockTypes_IPMetalMultiblock.DISTILLATION_TOWER_PARENT.getMeta()), PetroleumJEI.DISTILLATION_TOWER);
        new MultiblockIngredient(new ItemStack(IPContent.blockMetalMultiblock, 1, BlockTypes_IPMetalMultiblock.PUMPJACK_PARENT.getMeta()), PetroleumMultiblockIngredients::renderPumpjack, PetroleumJEI.RESERVOIR);
    }

    private static void renderPumpjack(int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 8, y + 8, 100);
        GlStateManager.scale(16, -16, 16);
        GlStateManager.rotate(20, 1, 0, 0);
        GlStateManager.rotate(-45, 0, 1, 0);
        float scale = 1F / 7F;
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.translate(-0.5, -1.25, -0.25);
        GlStateManager.enableRescaleNormal();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.color(1, 1, 1, 1);
        PUMPJACK.render(null, 0, 0, 0, 0, 0, 1);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }
}
