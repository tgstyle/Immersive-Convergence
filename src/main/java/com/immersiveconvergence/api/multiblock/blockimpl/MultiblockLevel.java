package com.immersiveconvergence.api.multiblock.blockimpl;

import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockLevel;
import com.immersiveconvergence.api.multiblock.blocks.util.MultiblockOrientation;
import com.immersiveconvergence.api.multiblock.blocks.util.RelativeBlockFace;
import com.immersiveconvergence.api.multiblock.util.SafeChunkUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public record MultiblockLevel(Supplier<Level> getLevel, MultiblockOrientation orientation, Supplier<BlockPos> origin) implements IMultiblockLevel {
    public MultiblockLevel(Supplier<Level> getLevel, MultiblockOrientation orientation, BlockPos origin) { this(getLevel, orientation, () -> origin); }

    @Override public BlockState getBlockState(BlockPos relativePosition) { return SafeChunkUtils.getBlockState(level(), toAbsolute(relativePosition)); }

    @Nullable @Override public BlockEntity getBlockEntity(BlockPos relativePosition) { return SafeChunkUtils.getSafeBE(level(), toAbsolute(relativePosition)); }

    @Nullable @Override public BlockEntity forciblyGetBlockEntity(BlockPos relativePosition) { return onLevel(level -> level.getBlockEntity(toAbsolute(relativePosition))); }

    @Override public boolean shouldTickModulo(int interval) {
        final int posRandom = 0x7f_ff_ff_ff & origin.hashCode();
        return posRandom % interval == onLevel(Level::getGameTime) % interval;
    }

    @Override public BlockPos getAbsoluteOrigin() { return origin.get(); }

    @Override public MultiblockOrientation getOrientation() { return orientation; }

    @Override public BlockPos toAbsolute(BlockPos relative) { return getAbsoluteOrigin().offset(orientation.getAbsoluteOffset(relative)); }

    @Override public @Nullable Direction toAbsolute(@Nullable RelativeBlockFace relative) {
        if (relative != null) { return relative.forFront(orientation); }
        else { return null; }
    }

    @Override public Vec3 toAbsolute(Vec3 relative) { return Vec3.atLowerCornerOf(getAbsoluteOrigin()).add(orientation.getAbsoluteOffset(relative)); }

    @Override public Level getRawLevel() { return level(); }

    @Override public void updateNeighbourForOutputSignal(BlockPos posInMultiblock) { updateNeighbourForOutputSignal(level(), toAbsolute(posInMultiblock)); }

    private static void updateNeighbourForOutputSignal(Level level, BlockPos absolutePos) {
        if (SafeChunkUtils.isChunkUnsafe(level, absolutePos)) { return; }
        level.updateNeighbourForOutputSignal(absolutePos, level.getBlockState(absolutePos).getBlock());
    }

    private <T> T onLevel(Function<Level, T> action) { return action.apply(level()); }

    private Level level() { return Objects.requireNonNull(getLevel.get()); }
}
