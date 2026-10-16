package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.block.BlockInterfaces;
import com.immersiveconvergence.api.util.IItemDropProvider;
import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistration;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockBE;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;
import com.immersiveconvergence.api.multiblock.blocks.registry.MultiblockPartBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Mth;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MachineMultiblockPartBlock<S extends IMultiblockState> extends MultiblockPartBlock<S> {
    private final MultiblockRegistration<S> multiblockRef;

    public MachineMultiblockPartBlock(Properties properties, MultiblockRegistration<S> multiblock) { super(properties, multiblock); this.multiblockRef = multiblock; }

    @Override public boolean isLadder(@Nonnull BlockState state, @Nonnull LevelReader level, @Nonnull BlockPos pos, @Nullable LivingEntity entity) {
        if (level.getBlockEntity(pos) instanceof IMultiblockBE<?> be && multiblockRef.logic() instanceof BlockInterfaces.ILadderPositionProvider ladderLogic) {
            BlockPos posInMB = be.getHelper().getPositionInMB();
            return posInMB != null && ladderLogic.isLadderPos(posInMB);
        }
        return false;
    }

    @Override public void entityInside(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (entity instanceof LivingEntity && isLadder(state, level, pos, (LivingEntity) entity)) { applyLadderLogic(entity); }
    }

    @Override @Nonnull public BlockState playerWillDestroy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull Player player) {
        if (!level.isClientSide) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof IMultiblockBE<?> be) {
                var helper = be.getHelper();
                if (((IDisassemblingAware) helper).ic$isAssembled()) {
                    if (te instanceof IItemDropProvider dropInv) {
                        dropInv.getDroppedItems().forEach(stack -> {
                            if (!stack.isEmpty()) {
                                ItemEntity item = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack);
                                level.addFreshEntity(item);
                            }
                        });
                    }
                    if (!(player instanceof FakePlayer)) {
                        QueueProcessor.currentlyBreakingPos = pos.immutable();
                        QueueProcessor.sneakBreaking = player.isShiftKeyDown();
                        try { return super.playerWillDestroy(level, pos, state, player); }
                        finally { QueueProcessor.currentlyBreakingPos = null; }
                    }
                }
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override public void onRemove(BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, BlockState newState, boolean isMoving) {
        try {
            if (!level.isClientSide && state.getBlock() != newState.getBlock()) {
                BlockEntity te = level.getBlockEntity(pos);
                if (te instanceof IMultiblockBE<?> be && ((IDisassemblingAware) be.getHelper()).ic$isAssembled()) {
                    QueueProcessor.currentlyBreakingPos = pos.immutable();
                    try { super.onRemove(state, level, pos, newState, isMoving); }
                    finally { QueueProcessor.currentlyBreakingPos = null; }
                    return;
                }
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
        finally { QueueProcessor.sneakBreaking = false; }
    }

    @Override @Nonnull
    public List<ItemStack> getDrops(@Nonnull BlockState state, @Nonnull LootParams.Builder builder) {
        List<ItemStack> drops = new ArrayList<>(super.getDrops(state, builder));
        BlockEntity te = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (te instanceof IItemDropProvider dropInv) { dropInv.getDroppedItems().forEach(drops::add); }
        return drops;
    }

    private static void applyLadderLogic(Entity entity) {
        if (entity instanceof LivingEntity living && !living.onClimbable()) {
            Vec3 motion = entity.getDeltaMovement();
            float maxMotion = 0.15F;
            motion = new Vec3(Mth.clamp(motion.x, -maxMotion, maxMotion), Math.max(motion.y, -maxMotion), Mth.clamp(motion.z, -maxMotion, maxMotion));
            entity.fallDistance = 0.0F;
            if (motion.y < 0 && entity instanceof Player && entity.isShiftKeyDown()) { motion = new Vec3(motion.x, 0, motion.z); }
            else if (entity.horizontalCollision) { motion = new Vec3(motion.x, 0.2, motion.z); }
            entity.setDeltaMovement(motion);
        }
    }
}
