package com.immersiveconvergence.api.multiblock.blocks.env;

import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistration;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;
import com.immersiveconvergence.api.multiblock.util.SetRestrictedField;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable public interface IMultiblockBEHelperDummy<State extends IMultiblockState> extends IMultiblockBEHelper<State> {
    SetRestrictedField<Factory> MAKE_HELPER = SetRestrictedField.common();

    void setPositionInMB(BlockPos pos);

    interface Factory {
        <T extends IMultiblockState>
        IMultiblockBEHelperDummy<T> makeFor(BlockEntity be, MultiblockRegistration<T> logic);
    }
}
