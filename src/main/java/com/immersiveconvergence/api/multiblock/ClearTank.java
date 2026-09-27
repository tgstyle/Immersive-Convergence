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

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "RedundantSuppression"}) public record ClearTank<S>(Supplier<? extends Collection<BlockPos>> pois, Consumer<S> clearAction, Component message) implements IMultiblockComponent<S> {
    public static Predicate<ItemStack> additionalTool = stack -> false;

    public ClearTank(List<BlockPos> pois, Consumer<S> clearAction, Component message) { this(constant(pois), clearAction, message); }

    private static Supplier<List<BlockPos>> constant(List<BlockPos> pois) {
        List<BlockPos> copy = ImmutableList.copyOf(pois);
        return () -> copy;
    }

    @Override public ItemInteractionResult click(IMultiblockContext<S> context, BlockPos posInMultiblock, Player player, InteractionHand hand, BlockHitResult absoluteHit, boolean isClient) {
        if (player.isShiftKeyDown() && pois.get().contains(posInMultiblock)) {
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
