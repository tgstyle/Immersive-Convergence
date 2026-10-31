package com.immersiveconvergence.mixin.client;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.client.renderer.ICWireSectionRenderer;

import net.minecraft.client.renderer.chunk.RenderRegionCache;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderRegionCache.class)
public abstract class RenderRegionCacheMixin {
    @Inject(method = "isAllEmpty(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;II[[Lnet/minecraft/client/renderer/chunk/RenderRegionCache$ChunkInfo;)Z", at = @At("HEAD"), cancellable = true)
    private static void ic$keepWiredSections(BlockPos start, BlockPos end, int startX, int startZ, RenderRegionCache.ChunkInfo[][] infos, CallbackInfoReturnable<Boolean> cir) {
        if (ICMods.immersiveEngineering()) { return; }
        if (ICWireSectionRenderer.hasWires(new BlockPos((start.getX() + end.getX()) >> 1, (start.getY() + end.getY()) >> 1, (start.getZ() + end.getZ()) >> 1))) { cir.setReturnValue(false); }
    }
}
