package com.immersiveconvergence.mixin;

import blusunrize.immersiveengineering.api.IEProperties.Model;
import blusunrize.immersiveengineering.client.models.split.BakedDynamicSplitModel;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Set;

@Mixin(BakedDynamicSplitModel.class)
public abstract class BakedDynamicSplitModelMixin {
    @Shadow(remap = false) @Final private static Set<BakedDynamicSplitModel<?, ?>> WEAK_INSTANCES;

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/google/common/cache/CacheBuilder;maximumSize(J)Lcom/google/common/cache/CacheBuilder;", remap = false), remap = false)
    private static long ic$largerCache(long maximumSize) { return 64L; }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/google/common/cache/CacheBuilder;expireAfterAccess(JLjava/util/concurrent/TimeUnit;)Lcom/google/common/cache/CacheBuilder;", remap = false), index = 0, remap = false)
    private static long ic$longerExpiry(long duration) { return 10L; }

    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void ic$registerForReloadClear(CallbackInfo ci) { WEAK_INSTANCES.add((BakedDynamicSplitModel<?, ?>) (Object) this); }

    @Inject(method = "getQuads", at = @At("HEAD"), cancellable = true, remap = false)
    private void ic$skipCulledSides(BlockState state, Direction side, RandomSource rand, ModelData data, RenderType renderType, CallbackInfoReturnable<List<BakedQuad>> cir) {
        if (side != null && data.get(Model.SUBMODEL_OFFSET) != null) { cir.setReturnValue(ImmutableList.of()); }
    }
}
