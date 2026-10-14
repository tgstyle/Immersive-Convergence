package com.immersiveconvergence.api.multiblock.blockimpl;

import com.immersiveconvergence.api.multiblock.blocks.env.IInitialMultiblockContext;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;
import com.immersiveconvergence.api.multiblock.blocks.util.MultiblockOrientation;
import com.immersiveconvergence.api.multiblock.blocks.util.RelativeBlockFace;
import com.immersiveconvergence.api.multiblock.util.CapabilityReference;
import com.immersiveconvergence.api.multiblock.util.DirectionalBlockPos;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.Nullable;
import java.util.function.Supplier;

public record InitialMultiblockContext<State extends IMultiblockState>(
        BlockEntity masterBE,
        MultiblockOrientation orientation,
        BlockPos masterOffset
) implements IInitialMultiblockContext<State> {
    @Override public <T> CapabilityReference<T> getCapabilityAt(
            Capability<T> capability, BlockPos posRelativeToMB, RelativeBlockFace face
    ) { return getCapabilityAt(masterBE, orientation, masterOffset, capability, posRelativeToMB, face); }

    @Override public Supplier<@Nullable Level> levelSupplier() { return masterBE::getLevel; }

    @Override public Runnable getMarkDirtyRunnable() { return masterBE::setChanged; }

    @Override public Runnable getSyncRunnable() { return () -> MultiblockContext.requestBESync(masterBE); }

    public static <T> CapabilityReference<T> getCapabilityAt(
            BlockEntity masterBE, MultiblockOrientation orientation, BlockPos masterOffset,
            Capability<T> capability, BlockPos posRelativeToMB, RelativeBlockFace face
    ) {
        return CapabilityReference.forBlockEntityAt(masterBE, () -> {
            final BlockPos offset = orientation.getAbsoluteOffset(posRelativeToMB.subtract(masterOffset));
            final BlockPos pos = masterBE.getBlockPos().offset(offset);
            final Direction absoluteFace = face.forFront(orientation);
            return new DirectionalBlockPos(pos, absoluteFace);
        }, capability);
    }
}
