package com.immersiveconvergence.mixin;

import com.immersiveconvergence.api.multiblock.QueueProcessor;

import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockPartBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import javax.annotation.Nonnull;

@Mixin(MultiblockPartBlock.class)
public abstract class MultiblockPartBlockMixin extends Block {
    private MultiblockPartBlockMixin(Properties properties) { super(properties); }

    @Override public int getLightBlock(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos) { return 0; }

    @Override public boolean propagatesSkylightDown(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos) { return true; }

    @Inject(method = "onRemove", at = @At("RETURN"))
    private void ic$clearBreakTracking(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving, CallbackInfo ci) {
        QueueProcessor.currentlyBreakingPos = null;
        QueueProcessor.sneakBreaking = false;
    }
}
