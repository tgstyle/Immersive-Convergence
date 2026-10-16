package com.immersiveconvergence.api.multiblock.util;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
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
}
