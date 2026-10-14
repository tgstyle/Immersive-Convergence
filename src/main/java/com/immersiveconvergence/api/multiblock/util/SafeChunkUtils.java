package com.immersiveconvergence.api.multiblock.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class SafeChunkUtils {
    private SafeChunkUtils() {}

    @Nullable private static LevelChunk getSafeChunk(LevelAccessor level, BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);
        return level.getChunkSource().getChunkNow(chunkPos.x, chunkPos.z);
    }

    public static boolean isChunkUnsafe(LevelAccessor level, BlockPos pos) { return getSafeChunk(level, pos) == null; }

    @Nullable public static BlockEntity getSafeBE(LevelAccessor level, BlockPos pos) {
        LevelChunk chunk = getSafeChunk(level, pos);
        return chunk == null ? null : chunk.getBlockEntity(pos);
    }

    @Nonnull public static BlockState getBlockState(LevelAccessor level, BlockPos pos) {
        LevelChunk chunk = getSafeChunk(level, pos);
        return chunk == null ? Blocks.AIR.defaultBlockState() : chunk.getBlockState(pos);
    }
}
