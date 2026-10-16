package com.immersiveconvergence.api.multiblock.blocks.env;

import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistration;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;
import com.immersiveconvergence.api.multiblock.util.SetRestrictedField;

import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import javax.annotation.Nonnull;

@NonExtendable public interface IMultiblockBEHelperMaster<State extends IMultiblockState> extends IMultiblockBEHelper<State> {
    SetRestrictedField<Factory> MAKE_HELPER = SetRestrictedField.common();

    @Nonnull @Override State getState();

    @Nonnull @Override IMultiblockContext<State> getContext();

    void tickServer();

    void tickClient();

    void invalidateAllCaps();

    interface Factory {
        <T extends IMultiblockState>
        IMultiblockBEHelperMaster<T> makeFor(BlockEntity be, MultiblockRegistration<T> logic);
    }
}
