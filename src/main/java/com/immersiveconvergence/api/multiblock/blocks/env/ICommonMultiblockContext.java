package com.immersiveconvergence.api.multiblock.blocks.env;

import com.immersiveconvergence.api.multiblock.blocks.util.MultiblockFace;
import com.immersiveconvergence.api.multiblock.blocks.util.RelativeBlockFace;
import com.immersiveconvergence.api.multiblock.util.CapabilityReference;

import net.minecraft.core.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@SuppressWarnings({"unused", "RedundantSuppression"}) @NonExtendable public interface ICommonMultiblockContext {
    default <T> CapabilityReference<T> getCapabilityAt(Capability<T> capability, MultiblockFace face) {
        return getCapabilityAt(capability, face.posInMultiblock(), face.face());
    }

    <T> CapabilityReference<T> getCapabilityAt(
            Capability<T> capability, BlockPos posRelativeToMB, RelativeBlockFace face
    );
}
