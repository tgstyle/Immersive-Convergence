package com.immersiveconvergence.mixin;

import blusunrize.immersiveengineering.api.IEProperties.Model;
import blusunrize.immersiveengineering.client.models.split.BakedBasicSplitModel;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;
import java.util.List;

@Mixin(BakedBasicSplitModel.class)
public abstract class BakedBasicSplitModelMixin implements BakedModel {
    @Shadow(remap = false) @Final private ItemTransforms itemTransforms;

    @Inject(method = "getQuads", at = @At("HEAD"), cancellable = true, remap = false)
    private void ic$skipCulledSides(BlockState state, Direction side, RandomSource rand, ModelData extraData, RenderType layer, CallbackInfoReturnable<List<BakedQuad>> cir) {
        if (side != null && extraData.get(Model.SUBMODEL_OFFSET) != null) { cir.setReturnValue(ImmutableList.of()); }
    }

    @Nonnull @Override
    public BakedModel applyTransform(@Nonnull ItemDisplayContext transformType, @Nonnull PoseStack poseStack, boolean applyLeftHandTransform) {
        itemTransforms.getTransform(transformType).apply(applyLeftHandTransform, poseStack);
        return this;
    }
}
