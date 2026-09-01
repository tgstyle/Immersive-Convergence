package com.immersiveconvergence.api.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IMultiblockComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.common.register.IEItems;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings({"unused", "RedundantSuppression"}) public record ClearTank<S>(List<BlockPos> pois, Consumer<S> clearAction, Component message) implements IMultiblockComponent<S> {
    public static Predicate<ItemStack> additionalTool = stack -> false;

    public ClearTank { pois = ImmutableList.copyOf(pois); }

    @Override public ItemInteractionResult click(IMultiblockContext<S> context, BlockPos posInMultiblock, Player player, InteractionHand hand, BlockHitResult absoluteHit, boolean isClient) {
        if (pois.contains(posInMultiblock) && player.isShiftKeyDown()) {
            ItemStack held = player.getItemInHand(hand);
            if (held.getItem() == IEItems.Tools.HAMMER.get() || additionalTool.test(held)) {
                if (!isClient) {
                    S state = context.getState();
                    clearAction.accept(state);
                    context.markMasterDirty();
                    context.requestMasterBESync();
                    player.displayClientMessage(message, true);
                }
                return ItemInteractionResult.sidedSuccess(isClient);
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
