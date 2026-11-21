package com.immersiveconvergence.api.multiblock.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import javax.annotation.Nullable;
import java.util.Objects;

public final class CapabilityUtils {
    private CapabilityUtils() {}

    public static <T> LazyOptional<T> constantOptional(T val) {
        LazyOptional<T> result = LazyOptional.of(() -> Objects.requireNonNull(val));
        result.resolve();
        return result;
    }

    @Nullable public static <T> T getCapability(ICapabilityProvider provider, Capability<T> cap, @Nullable Direction side) { return provider.getCapability(cap, side).resolve().orElse(null); }

    @Nullable public static IEnergyStorage neighborEnergy(Level level, BlockPos pos, Direction facing) {
        BlockEntity tile = SafeChunkUtils.getSafeBE(level, pos.relative(facing));
        return tile == null ? null : getCapability(tile, ForgeCapabilities.ENERGY, facing.getOpposite());
    }
}
