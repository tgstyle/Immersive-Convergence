package com.immersiveconvergence.api.compat;

import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistration;
import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistration.ExtraComponent;
import com.immersiveconvergence.api.multiblock.blocks.component.RedstoneControl;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockBEHelper;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockBE;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;
import com.immersiveconvergence.api.multiblock.util.ComputerControlState;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class ICComputerControl {
    private static final String GET_ENABLED = "getEnabled";
    private static final String SET_ENABLED = "setEnabled";
    private static final String ENABLE_COMPUTER_CONTROL = "enableComputerControl";

    private ICComputerControl() {}

    @SuppressWarnings("unused") public static <S extends IMultiblockState> ICComputerTable<S> multiblock(String type, MultiblockRegistration<S> registration, Predicate<BlockPos> attach) {
        ICComputerTable<S> table = new ICComputerTable<>(type, List.of(registration.masterBE(), registration.dummyBE()), blockEntity -> {
            IMultiblockBEHelper<S> helper = helper(blockEntity, registration);
            return helper != null && attach.test(helper.getPositionInMB());
        }, blockEntity -> {
            IMultiblockBEHelper<S> helper = helper(blockEntity, registration);
            return helper == null ? null : helper.getState();
        });
        RedstoneControl<S> control = control(registration);
        return control == null ? table : control(table, control);
    }

    public static Predicate<BlockPos> at(Supplier<BlockPos> position) { return pos -> pos.equals(position.get()); }

    public static Predicate<BlockPos> anywhere() { return pos -> true; }

    @SuppressWarnings("unused") public static Predicate<BlockPos> atAny(Supplier<Collection<BlockPos>> positions) {
        return pos -> {
            Collection<BlockPos> valid = positions.get();
            return valid != null && valid.contains(pos);
        };
    }

    private static <S extends IMultiblockState> ICComputerTable<S> control(ICComputerTable<S> table, RedstoneControl<S> control) {
        table.add(GET_ENABLED, state -> state(control, state).isEnabled());
        table.call(SET_ENABLED, (state, args) -> {
            state(control, state).setEnabled(ICComputerArgs.checkBoolean(args, 0));
            return null;
        });
        table.call(ENABLE_COMPUTER_CONTROL, (state, args) -> {
            enableComputerControl(state(control, state), ICComputerArgs.checkBoolean(args, 0));
            return null;
        });
        return table.connection(state -> state(control, state).addReference(), state -> state(control, state).removeReference());
    }

    public static void enableComputerControl(ComputerControlState computer, boolean enable) {
        if (!enable) {
            computer.clear();
            return;
        }
        if (!computer.isAttached()) { computer.addReference(); }
        computer.setEnabled(true);
    }

    private static <S> ComputerControlState state(RedstoneControl<S> control, S state) { return control.wrapState(state).getComputerControlState(); }

    @SuppressWarnings("unchecked") @Nullable private static <S extends IMultiblockState> RedstoneControl<S> control(MultiblockRegistration<S> registration) {
        for (ExtraComponent<S, ?> component : registration.extraComponents()) {
            if (component.makeWrapper() instanceof RedstoneControl<?> control) { return (RedstoneControl<S>) control; }
        }
        return null;
    }

    @SuppressWarnings("unchecked") @Nullable private static <S extends IMultiblockState> IMultiblockBEHelper<S> helper(BlockEntity blockEntity, MultiblockRegistration<S> registration) {
        if (!(blockEntity instanceof IMultiblockBE<?> multiblock) || multiblock.getHelper().getMultiblock() != registration) { return null; }
        return (IMultiblockBEHelper<S>) multiblock.getHelper();
    }
}
