package com.immersiveconvergence.api.multiblock.blocks.component;

import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockContext;
import com.immersiveconvergence.api.multiblock.blocks.util.CapabilityPosition;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings({"unused", "RedundantSuppression"}) public interface IMultiblockComponent<State> {
    default void registerCapabilities(CapabilityRegistrar<State> register) {
    }

    default void onEntityCollision(IMultiblockContext<State> ctx, BlockPos posInMultiblock, Entity collided) {
    }

    default ItemInteractionResult click(
            IMultiblockContext<State> ctx, BlockPos posInMultiblock, Player player,
            InteractionHand hand,
            BlockHitResult absoluteHit,
            boolean isClient
    ) { return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION; }

    default void dropExtraItems(State state, Consumer<ItemStack> drop) {
    }

    default void onRemoved(IMultiblockContext<State> context) {
    }

    interface StateWrapper<OuterState, OwnState> {
        OwnState wrapState(OuterState outer);
    }

    interface CapabilityRegistrar<State> {
        <T> void register(BlockCapability<T, @Nullable Direction> capability, CapabilityGetter<T, State> getter);

        default <T> void registerAtBlockPos(
                BlockCapability<T, @Nullable Direction> capability,
                BlockPos atPosition,
                Function<State, T> getter
        ) { register(capability, (state, position) -> Objects.equals(position.posInMultiblock(), atPosition) ? getter.apply(state) : null); }

        default <T> void registerAt(
                BlockCapability<T, @Nullable Direction> capability,
                CapabilityPosition atPosition,
                Function<State, T> getter
        ) { register(capability, (state, position) -> Objects.equals(position, atPosition) ? getter.apply(state) : null); }

        default <T> void registerAtOrNull(
                BlockCapability<T, @Nullable Direction> capability,
                CapabilityPosition atPosition,
                Function<State, T> getter
        ) { register(capability, (state, position) -> atPosition.equalsOrNullFace(position) ? getter.apply(state) : null); }

        default <T> void registerEverywhere(
                BlockCapability<T, @Nullable Direction> capability, Function<State, T> getter
        ) { register(capability, (state, position) -> getter.apply(state)); }
    }

    interface CapabilityGetter<T, State> {
        @Nullable T getCapability(State state, CapabilityPosition position);
    }
}
