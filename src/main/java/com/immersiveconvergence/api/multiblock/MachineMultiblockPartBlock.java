package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistration;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockBE;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;
import com.immersiveconvergence.api.multiblock.blocks.registry.MultiblockPartBlock;
import com.immersiveconvergence.api.util.IItemDropProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.common.util.FakePlayer;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class MachineMultiblockPartBlock<S extends IMultiblockState> extends MultiblockPartBlock<S> {
    public MachineMultiblockPartBlock(Properties properties, MultiblockRegistration<S> multiblock) { super(properties, multiblock); }

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

    @SuppressWarnings("deprecation") @Override @Nonnull public List<ItemStack> getDrops(@Nonnull BlockState state, @Nonnull LootParams.Builder builder) {
        List<ItemStack> drops = new ArrayList<>(super.getDrops(state, builder));
        BlockEntity te = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (te instanceof IItemDropProvider dropInv) { dropInv.getDroppedItems().forEach(drops::add); }
        return drops;
    }
}
