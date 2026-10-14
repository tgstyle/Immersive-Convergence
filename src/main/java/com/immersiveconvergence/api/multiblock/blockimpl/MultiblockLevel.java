package com.immersiveconvergence.api.multiblock.blockimpl;

import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockLevel;
import com.immersiveconvergence.api.multiblock.blocks.util.MultiblockOrientation;
import com.immersiveconvergence.api.multiblock.blocks.util.RelativeBlockFace;
import com.immersiveconvergence.api.multiblock.util.CapabilityUtils;
import com.immersiveconvergence.api.multiblock.util.SafeChunkUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "RedundantSuppression"}) public record MultiblockLevel(Supplier<Level> getLevel, MultiblockOrientation orientation, Supplier<BlockPos> origin) implements IMultiblockLevel {
    public MultiblockLevel(Supplier<Level> getLevel, MultiblockOrientation orientation, BlockPos origin) { this(getLevel, orientation, () -> origin); }

    @Override public BlockState getBlockState(BlockPos relativePosition) { return SafeChunkUtils.getBlockState(level(), toAbsolute(relativePosition)); }

    @Override public void setBlock(BlockPos relativePosition, BlockState state) { onLevel(level -> level.setBlock(toAbsolute(relativePosition), state, Block.UPDATE_ALL)); }

    @Nullable @Override public BlockEntity getBlockEntity(BlockPos relativePosition) { return SafeChunkUtils.getSafeBE(level(), toAbsolute(relativePosition)); }

    @Nullable @Override public BlockEntity forciblyGetBlockEntity(BlockPos relativePosition) { return onLevel(level -> level.getBlockEntity(toAbsolute(relativePosition))); }

    @Override @Nullable public <T> T getCapabilityValue(Capability<T> capability, BlockPos relativePosition, @Nullable RelativeBlockFace face) {
        final BlockEntity blockEntity = getBlockEntity(relativePosition);
        if (blockEntity == null) { return null; }
        final Direction absoluteFace = toAbsolute(face);
        return CapabilityUtils.getCapability(blockEntity, capability, absoluteFace);
    }

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

    @Override public AABB toAbsolute(AABB relative) {
        final Vec3 minPos = new Vec3(relative.minX, relative.minY, relative.minZ);
        final Vec3 maxPos = new Vec3(relative.maxX, relative.maxY, relative.maxZ);
        return new AABB(toAbsolute(minPos), toAbsolute(maxPos));
    }

    @Override public Vec3 toAbsolute(Vec3 relative) { return Vec3.atLowerCornerOf(getAbsoluteOrigin()).add(orientation.getAbsoluteOffset(relative)); }

    @Override public BlockPos toRelative(BlockPos absolute) {
        final BlockPos absoluteOffset = absolute.subtract(getAbsoluteOrigin());
        return orientation.getPosInMB(absoluteOffset);
    }

    @Override public RelativeBlockFace toRelative(Direction absolute) { return RelativeBlockFace.from(orientation, absolute); }

    @Override public boolean isThundering() { return onLevel(Level::isThundering); }

    @Override public boolean isRaining() { return onLevel(Level::isRaining); }

    @Override public int getMaxBuildHeight() { return onLevel(Level::getMaxBuildHeight); }

    @Override public Level getRawLevel() { return level(); }

    @Override public void updateNeighbourForOutputSignal(BlockPos posInMultiblock) { updateNeighbourForOutputSignal(level(), toAbsolute(posInMultiblock)); }

    private static void updateNeighbourForOutputSignal(Level level, BlockPos absolutePos) {
        if (SafeChunkUtils.isChunkUnsafe(level, absolutePos)) { return; }
        level.updateNeighbourForOutputSignal(absolutePos, level.getBlockState(absolutePos).getBlock());
    }

    private <T> T onLevel(Function<Level, T> action) { return action.apply(level()); }

    private Level level() { return Objects.requireNonNull(getLevel.get()); }
}
