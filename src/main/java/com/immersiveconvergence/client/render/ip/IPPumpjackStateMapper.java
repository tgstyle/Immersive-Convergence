package com.immersiveconvergence.client.render.ip;

import blusunrize.immersiveengineering.client.IECustomStateMapper;
import flaxbeard.immersivepetroleum.common.blocks.BlockIPMetalMultiblocks;
import flaxbeard.immersivepetroleum.common.blocks.metal.BlockTypes_IPMetalMultiblock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import javax.annotation.Nonnull;

public class IPPumpjackStateMapper extends IECustomStateMapper {
    public static final ResourceLocation FILE = new ResourceLocation("immersivepetroleum", "metal_multiblock_pumpjackparent");

    @Override @Nonnull protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
        BlockTypes_IPMetalMultiblock type = state.getValue(((BlockIPMetalMultiblocks)state.getBlock()).property);
        if (type == BlockTypes_IPMetalMultiblock.PUMPJACK_PARENT) { return new ModelResourceLocation(FILE, getPropertyString(state.getProperties())); }
        return super.getModelResourceLocation(state);
    }
}
