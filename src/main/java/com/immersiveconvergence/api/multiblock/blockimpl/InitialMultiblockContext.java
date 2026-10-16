package com.immersiveconvergence.api.multiblock.blockimpl;

import com.immersiveconvergence.api.multiblock.blocks.env.IInitialMultiblockContext;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;
import com.immersiveconvergence.api.multiblock.blocks.util.MultiblockOrientation;
import com.immersiveconvergence.api.multiblock.blocks.util.RelativeBlockFace;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import org.jetbrains.annotations.Nullable;
import java.util.function.Supplier;

public record InitialMultiblockContext<State extends IMultiblockState>(BlockEntity masterBE, MultiblockOrientation orientation, BlockPos masterOffset) implements IInitialMultiblockContext<State> {
    @Override public <T, C>
    Supplier<T> getCapabilityAt(BlockCapability<T, C> capability, BlockPos posRelativeToMB, C context) { return getCapabilityAt(masterBE, orientation, masterOffset, capability, posRelativeToMB, context); }

    @Override public <T> Supplier<T> getCapabilityAt(BlockCapability<T, Direction> capability, BlockPos posRelativeToMB, RelativeBlockFace face) { return getCapabilityAt(capability, posRelativeToMB, face.forFront(orientation)); }

    @Override public Supplier<@Nullable Level> levelSupplier() { return masterBE::getLevel; }

    @Override public Runnable getMarkDirtyRunnable() { return masterBE::setChanged; }

    @Override public Runnable getSyncRunnable() { return () -> MultiblockContext.requestBESync(masterBE); }

    public static <T, C> Supplier<@Nullable T> getCapabilityAt(BlockEntity masterBE, MultiblockOrientation orientation, BlockPos masterOffset, BlockCapability<T, C> capability, BlockPos posRelativeToMB, C context) {
        return new Supplier<>() {
            private BlockCapabilityCache<T, C> cache;

            @Override @Nullable public T get() {
                Level level = masterBE.getLevel();
                if (level == null) { return null; }
                if (level instanceof ServerLevel serverLevel) {
                    if (cache == null) { cache = BlockCapabilityCache.create(capability, serverLevel, getCapabilityPos(), context); }
                    return cache.getCapability();
                }
                else { return level.getCapability(capability, getCapabilityPos(), context); }
            }

            private BlockPos getCapabilityPos() {
                final BlockPos offset = orientation.getAbsoluteOffset(posRelativeToMB.subtract(masterOffset));
                return masterBE.getBlockPos().offset(offset);
            }
        };
    }
}
