package com.immersiveconvergence.api.fluid;

import com.immersiveconvergence.core.config.ICCommonConfig;

import net.minecraftforge.fluids.Fluid;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused") public final class ICPipes {
    private static final Set<Fluid> NORMALLY_PRESSURIZED = Collections.synchronizedSet(new HashSet<>());

    private ICPipes() {}

    public static int getTransferableAmount(boolean pressurized) { return pressurized ? ICCommonConfig.pipes.pressurizedTransferRate : ICCommonConfig.pipes.transferRate; }

    public static boolean lastServed() { return ICCommonConfig.pipes.lastServed; }

    public static boolean isNormallyPressurized(Fluid fluid) { return fluid != null && NORMALLY_PRESSURIZED.contains(fluid); }

    public static void addNormallyPressurized(Fluid fluid) {
        if (fluid != null) { NORMALLY_PRESSURIZED.add(fluid); }
    }

    public static void removeNormallyPressurized(Fluid fluid) { NORMALLY_PRESSURIZED.remove(fluid); }
}
