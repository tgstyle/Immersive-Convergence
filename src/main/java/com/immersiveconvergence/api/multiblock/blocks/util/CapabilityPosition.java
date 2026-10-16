package com.immersiveconvergence.api.multiblock.blocks.util;

import net.minecraft.core.BlockPos;
import javax.annotation.Nullable;

public record CapabilityPosition(BlockPos posInMultiblock, @Nullable RelativeBlockFace side) {
    public static CapabilityPosition opposing(MultiblockFace multiblockPos) {
        return new CapabilityPosition(
                multiblockPos.face().offsetRelative(multiblockPos.posInMultiblock(), 1),
                multiblockPos.face().getOpposite()
        );
    }
}
