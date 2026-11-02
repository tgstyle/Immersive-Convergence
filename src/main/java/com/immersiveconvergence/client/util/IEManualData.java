package com.immersiveconvergence.client.util;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks.MultiblockManualData;
import com.immersiveconvergence.api.multiblock.client.ClientMultiblocks;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public record IEManualData(ClientMultiblocks.MultiblockManualData data) implements MultiblockManualData {
    @Override public NonNullList<ItemStack> getTotalMaterials() { return data.getTotalMaterials(); }

    @Override public boolean canRenderFormedStructure() { return data.canRenderFormedStructure(); }

    @Override public void renderFormedStructure(PoseStack transform, MultiBufferSource buffer) { data.renderFormedStructure(transform, buffer); }
}
