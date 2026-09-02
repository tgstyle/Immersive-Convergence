package com.immersiveconvergence.mixin;

import blusunrize.immersiveengineering.client.models.split.AbstractSplitModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.util.TriState;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.Nonnull;

@Mixin(AbstractSplitModel.class)
public abstract class AbstractSplitModelMixin implements BakedModel {
    @Override public boolean useAmbientOcclusion() { return false; }

    @Nonnull @Override public TriState useAmbientOcclusion(@Nonnull BlockState state, @Nonnull ModelData data, @Nonnull RenderType renderType) { return TriState.FALSE; }
}
