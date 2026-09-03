package com.immersiveconvergence.api.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockBE;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockPartBlock;
import com.immersiveconvergence.api.block.BlockInterfaces;
import com.immersiveconvergence.api.util.IItemDropProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class MachineMultiblockPartBlock<S extends IMultiblockState> extends MultiblockPartBlock<S> {
    public MachineMultiblockPartBlock(Properties properties, MultiblockRegistration<S> multiblock) { super(properties, multiblock); }

    @SuppressWarnings("deprecation")
    @Override public int getLightBlock(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos) { return 0; }

    @Override public boolean propagatesSkylightDown(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos) { return true; }

    @Nonnull @Override public InteractionResult use(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof BlockInterfaces.IPlayerInteraction be) {
            Vec3 hitVec = hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
            if (be.interact(hit.getDirection(), player, hand, player.getItemInHand(hand), (float) hitVec.x, (float) hitVec.y, (float) hitVec.z)) { return InteractionResult.sidedSuccess(level.isClientSide); }
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override public void playerWillDestroy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull Player player) {
        if (level.isClientSide) { return; }
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof IMultiblockBE<?> be) {
            var helper = be.getHelper();
            if (((IDisassemblingAware) helper).ic$isDisassembling()) {
                super.playerWillDestroy(level, pos, state, player);
                return;
            }
            if (te instanceof IItemDropProvider dropInv) {
                dropInv.getDroppedItems().forEach(stack -> {
                    if (!stack.isEmpty()) {
                        ItemEntity item = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack);
                        level.addFreshEntity(item);
                    }
                });
            }
            if (helper.getContext() != null && ((IDisassemblingAware) helper).ic$isAssembled() && !(player instanceof FakePlayer)) {
                QueueProcessor.currentlyBreakingPos = pos.immutable();
                QueueProcessor.sneakBreaking = player.isShiftKeyDown();
                try { helper.disassemble(); }
                finally {
                    QueueProcessor.currentlyBreakingPos = null;
                    QueueProcessor.sneakBreaking = false;
                }
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override public void onRemove(BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof IMultiblockBE<?> be) {
                var helper = be.getHelper();
                if (((IDisassemblingAware) helper).ic$isDisassembling()) {
                    super.onRemove(state, level, pos, newState, isMoving);
                    return;
                }
                if (helper.getContext() != null && ((IDisassemblingAware) helper).ic$isAssembled()) {
                    QueueProcessor.currentlyBreakingPos = pos.immutable();
                    try { helper.disassemble(); }
                    finally { QueueProcessor.currentlyBreakingPos = null; }
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @SuppressWarnings("deprecation")
    @Override @Nonnull public List<ItemStack> getDrops(@Nonnull BlockState state, @Nonnull LootParams.Builder builder) {
        List<ItemStack> drops = new ArrayList<>(super.getDrops(state, builder));
        BlockEntity te = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (te instanceof IItemDropProvider dropInv) {
            dropInv.getDroppedItems().forEach(drops::add);
        }
        return drops;
    }
}
