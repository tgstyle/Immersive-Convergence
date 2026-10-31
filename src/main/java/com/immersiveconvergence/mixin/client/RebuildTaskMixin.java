package com.immersiveconvergence.mixin.client;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.client.renderer.ICWireSectionRenderer;

import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@SuppressWarnings("ModifyVariableMayUseName")
@Mixin(targets = "net.minecraft.client.renderer.chunk.ChunkRenderDispatcher$RenderChunk$RebuildTask")
public abstract class RebuildTaskMixin {
    @Unique private BlockPos ic$origin;
    @Unique private RenderChunkRegion ic$region;
    @Unique private Set<RenderType> ic$layers;

    @ModifyVariable(method = "compile(FFFLnet/minecraft/client/renderer/ChunkBufferBuilderPack;)Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk$RebuildTask$CompileResults;", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;create()Lnet/minecraft/util/RandomSource;"), ordinal = 0)
    private BlockPos ic$keepOrigin(BlockPos blockpos) {
        ic$origin = blockpos;
        return blockpos;
    }

    @ModifyVariable(method = "compile(FFFLnet/minecraft/client/renderer/ChunkBufferBuilderPack;)Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk$RebuildTask$CompileResults;", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;create()Lnet/minecraft/util/RandomSource;"))
    private RenderChunkRegion ic$keepRegion(RenderChunkRegion renderchunkregion) {
        ic$region = renderchunkregion;
        return renderchunkregion;
    }

    @ModifyVariable(method = "compile(FFFLnet/minecraft/client/renderer/ChunkBufferBuilderPack;)Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk$RebuildTask$CompileResults;", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;create()Lnet/minecraft/util/RandomSource;"))
    private Set<RenderType> ic$keepLayers(Set<RenderType> set) {
        ic$layers = set;
        return set;
    }

    @Inject(method = "compile(FFFLnet/minecraft/client/renderer/ChunkBufferBuilderPack;)Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk$RebuildTask$CompileResults;", at = @At(value = "INVOKE", target = "Ljava/util/Set;iterator()Ljava/util/Iterator;", remap = false))
    private void ic$addWires(float x, float y, float z, ChunkBufferBuilderPack chunkBufferBuilderPack, CallbackInfoReturnable<?> cir) {
        if (!ICMods.immersiveEngineering() && ic$origin != null && ic$region != null && ic$layers != null) { ICWireSectionRenderer.renderSection(ic$origin, ic$layers, chunkBufferBuilderPack, ic$region); }
        ic$origin = null;
        ic$region = null;
        ic$layers = null;
    }
}
