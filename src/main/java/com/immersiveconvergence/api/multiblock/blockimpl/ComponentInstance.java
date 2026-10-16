package com.immersiveconvergence.api.multiblock.blockimpl;

import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistration.ExtraComponent;
import com.immersiveconvergence.api.multiblock.blocks.component.IClientTickableComponent;
import com.immersiveconvergence.api.multiblock.blocks.component.IMultiblockComponent;
import com.immersiveconvergence.api.multiblock.blocks.component.IServerTickableComponent;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockContext;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public record ComponentInstance<S>(IMultiblockComponent<S> component, IMultiblockContext<S> wrappedContext) {
    @SuppressWarnings("unchecked") public static <S, O> ComponentInstance<S> make(ExtraComponent<O, S> c, O state, IMultiblockContext<?> ctx) {
        final S wrappedState = c.makeWrapper().wrapState(state);
        IMultiblockContext<S> wrapped;
        if (wrappedState == state) { wrapped = (IMultiblockContext<S>)ctx; }
        else { wrapped = new WrappingMultiblockContext<>(ctx, wrappedState); }
        return new ComponentInstance<>(c.component(), wrapped);
    }

    public ItemInteractionResult click(BlockPos positionInMB, Player player, InteractionHand hand, BlockHitResult hit, boolean isClientSide) { return component.click(wrappedContext, positionInMB, player, hand, hit, isClientSide); }

    public void tickServer() {
        if (component instanceof IServerTickableComponent<S> serverTickable) { serverTickable.tickServer(wrappedContext); }
    }

    public void tickClient() {
        if (component instanceof IClientTickableComponent<S> clientTickable) { clientTickable.tickClient(wrappedContext); }
    }

    public S state() { return wrappedContext.getState(); }
}
