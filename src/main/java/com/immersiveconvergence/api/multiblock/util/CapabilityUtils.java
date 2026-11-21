package com.immersiveconvergence.api.multiblock.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import javax.annotation.Nullable;

public final class CapabilityUtils {
    private CapabilityUtils() {}

    @Nullable public static IEnergyStorage neighborEnergy(Level level, BlockPos pos, Direction facing) {
        BlockPos neighbor = pos.relative(facing);
        return SafeChunkUtils.isChunkUnsafe(level, neighbor) ? null : level.getCapability(Capabilities.EnergyStorage.BLOCK, neighbor, facing.getOpposite());
    }
}
