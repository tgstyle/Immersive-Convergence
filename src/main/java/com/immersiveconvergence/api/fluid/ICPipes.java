package com.immersiveconvergence.api.fluid;

import com.immersiveconvergence.core.config.ICCommonConfig;

import net.minecraft.world.level.material.Fluid;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class ICPipes {
    private static final Set<Fluid> NORMALLY_PRESSURIZED = Collections.synchronizedSet(new HashSet<>());

    private ICPipes() {}

    public static int getTransferableAmount(boolean pressurized) { return pressurized ? ICCommonConfig.pipePressurizedTransferRate : ICCommonConfig.pipeTransferRate; }

    public static boolean lastServed() { return ICCommonConfig.pipeLastServed; }

    public static boolean isNormallyPressurized(@Nullable Fluid fluid) { return fluid != null && NORMALLY_PRESSURIZED.contains(fluid); }

    public static void addNormallyPressurized(@Nullable Fluid fluid) { if (fluid != null) { NORMALLY_PRESSURIZED.add(fluid); } }
}
