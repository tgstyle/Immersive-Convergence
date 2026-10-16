package com.immersiveconvergence.api.multiblock.blocks.component;

import com.immersiveconvergence.api.block.BlockToolGates;
import com.immersiveconvergence.api.multiblock.blocks.component.IMultiblockComponent.StateWrapper;
import com.immersiveconvergence.api.multiblock.blocks.component.RedstoneControl.RSState;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockContext;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;
import com.immersiveconvergence.api.multiblock.util.ComputerControlState;
import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import java.util.Arrays;
import java.util.List;

public class RedstoneControl<S> implements IMultiblockComponent<RSState>, StateWrapper<S, RSState> {
    private static final String CHAT_INFO = "chat." + ICLib.MODID + ".info.";
    private final StateWrapper<S, RSState> getState;
    private final List<BlockPos> positions;

    public RedstoneControl(StateWrapper<S, RSState> getState, BlockPos... positions) {
        this.positions = Arrays.asList(positions);
        this.getState = s -> {
            final RSState rsState = getState.wrapState(s);
            rsState.positions = this.positions;
            return rsState;
        };
    }

    @Override public RSState wrapState(S outer) { return getState.wrapState(outer); }

    @Override public ItemInteractionResult click(IMultiblockContext<RSState> ctx, BlockPos posInMultiblock, Player player, InteractionHand hand, BlockHitResult absoluteHit, boolean isClient) {
        final ItemStack held = player.getItemInHand(hand);
        if (!BlockToolGates.isScrewdriver.test(held) || !positions.contains(posInMultiblock)) { return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION; }
        if (!isClient) {
            final boolean inverted = !ctx.getState().rsEnablesMachine;
            ctx.getState().rsEnablesMachine = inverted;
            player.displayClientMessage(
                    Component.translatable(CHAT_INFO + "rsControl." + (inverted ? "invertedOn" : "invertedOff")), true
            );
            ctx.markMasterDirty();
        }
        return ItemInteractionResult.SUCCESS;
    }

    public static class RSState implements IMultiblockState {
        private boolean rsEnablesMachine;
        private List<BlockPos> positions = List.of();
        private final ComputerControlState computerControlState = new ComputerControlState();

        public static RSState enabledByDefault() { return new RSState(false); }

        private RSState(boolean rsEnablesMachine) { this.rsEnablesMachine = rsEnablesMachine; }

        public boolean isEnabled(IMultiblockContext<?> ctx) {
            if (computerControlState.isAttached() && !computerControlState.isEnabled()) { return false; }
            boolean hasRS = false;
            for (final BlockPos rsPos : positions) { hasRS = hasRS || ctx.getRedstoneInputValue(rsPos, 0) > 0; }
            return rsEnablesMachine == hasRS;
        }

        @Override public void writeSaveNBT(CompoundTag nbt, Provider provider) { nbt.putBoolean("rsEnablesMachine", rsEnablesMachine); }

        @Override public void readSaveNBT(CompoundTag nbt, Provider provider) { rsEnablesMachine = nbt.getBoolean("rsEnablesMachine"); }

        public ComputerControlState getComputerControlState() { return computerControlState; }
    }
}
