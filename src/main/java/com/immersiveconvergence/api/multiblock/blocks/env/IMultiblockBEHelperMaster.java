package com.immersiveconvergence.api.multiblock.blocks.env;

import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistration;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;
import com.immersiveconvergence.api.multiblock.blocks.registry.MultiblockBlockEntityMaster;
import com.immersiveconvergence.api.multiblock.util.SetRestrictedField;

import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import javax.annotation.Nonnull;

@NonExtendable public interface IMultiblockBEHelperMaster<State extends IMultiblockState> extends IMultiblockBEHelper<State> {
    SetRestrictedField<Factory> MAKE_HELPER = SetRestrictedField.common();

    @Nonnull @Override State getState();

    @Nonnull @Override IMultiblockContext<State> getContext();

    AABB getRenderBoundingBox();

    void tickServer();

    void tickClient();

    void onRemoved();

    void invalidateAllCaps();

    interface Factory {
        <T extends IMultiblockState>
        IMultiblockBEHelperMaster<T> makeFor(MultiblockBlockEntityMaster<T> be, MultiblockRegistration<T> logic);
    }
}
