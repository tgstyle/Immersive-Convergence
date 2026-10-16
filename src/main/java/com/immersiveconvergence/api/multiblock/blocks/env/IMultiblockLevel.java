package com.immersiveconvergence.api.multiblock.blocks.env;

import com.immersiveconvergence.api.multiblock.blocks.util.MultiblockOrientation;
import com.immersiveconvergence.api.multiblock.blocks.util.RelativeBlockFace;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import javax.annotation.Nullable;

@NonExtendable public interface IMultiblockLevel {
    BlockState getBlockState(BlockPos relativePosition);

    @Nullable BlockEntity getBlockEntity(BlockPos relativePosition);

    @Nullable BlockEntity forciblyGetBlockEntity(BlockPos relativePosition);

    boolean shouldTickModulo(int interval);

    BlockPos getAbsoluteOrigin();

    MultiblockOrientation getOrientation();

    BlockPos toAbsolute(BlockPos relative);

    @Nullable Direction toAbsolute(@Nullable RelativeBlockFace relative);

    Vec3 toAbsolute(Vec3 relative);

    Level getRawLevel();

    void updateNeighbourForOutputSignal(BlockPos posInMultiblock);
}
