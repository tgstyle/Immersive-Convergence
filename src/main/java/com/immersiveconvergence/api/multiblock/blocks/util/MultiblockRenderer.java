package com.immersiveconvergence.api.multiblock.blocks.util;

import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockContext;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;
import com.immersiveconvergence.api.multiblock.blocks.registry.MultiblockBlockEntityMaster;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"unused", "RedundantSuppression"}) public interface MultiblockRenderer<State extends IMultiblockState> extends BlockEntityRenderer<MultiblockBlockEntityMaster<State>> {
    @Override default void render(
            @NotNull MultiblockBlockEntityMaster<State> te,
            float partialTicks, @NotNull PoseStack matrixStack, @NotNull MultiBufferSource bufferIn,
            int combinedLightIn, int combinedOverlayIn
    ) { render(te.getHelper().getContext(), partialTicks, matrixStack, bufferIn, combinedLightIn, combinedOverlayIn); }

    @Override @NotNull default AABB getRenderBoundingBox(@NotNull MultiblockBlockEntityMaster<State> blockEntity) {
        return blockEntity.getHelper().getRenderBoundingBox();
    }

    void render(
            @NotNull IMultiblockContext<State> ctx,
            float partialTicks, @NotNull PoseStack matrixStack, @NotNull MultiBufferSource bufferIn,
            int combinedLightIn, int combinedOverlayIn
    );
}
