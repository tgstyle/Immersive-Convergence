package com.immersiveconvergence.api.multiblock.blocks.component;

import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockContext;
import com.immersiveconvergence.api.multiblock.blocks.util.CapabilityPosition;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;
import java.util.function.Consumer;

public interface IMultiblockComponent<State> {
    default void registerCapabilities(CapabilityRegistrar<State> register) {}

    default ItemInteractionResult click(IMultiblockContext<State> ctx, BlockPos posInMultiblock, Player player, InteractionHand hand, BlockHitResult absoluteHit, boolean isClient) { return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION; }

    default void dropExtraItems(State state, Consumer<ItemStack> drop) {}

    interface StateWrapper<OuterState, OwnState> {
        OwnState wrapState(OuterState outer);
    }

    interface CapabilityRegistrar<State> {
        <T> void register(BlockCapability<T, @Nullable Direction> capability, CapabilityGetter<T, State> getter);
    }

    interface CapabilityGetter<T, State> {
        @Nullable T getCapability(State state, CapabilityPosition position);
    }
}
