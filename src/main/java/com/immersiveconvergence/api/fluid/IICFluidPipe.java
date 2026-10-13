package com.immersiveconvergence.api.fluid;

import net.neoforged.neoforge.fluids.FluidType;

@SuppressWarnings("unused") public interface IICFluidPipe {
    int AMOUNT_PRESSURIZED = FluidType.BUCKET_VOLUME;
    int AMOUNT_UNPRESSURIZED = FluidType.BUCKET_VOLUME / 20;
    String NBT_PRESSURIZED = "pressurized";

    static int getTransferableAmount(boolean pressurized) { return pressurized ? AMOUNT_PRESSURIZED : AMOUNT_UNPRESSURIZED; }

    default boolean stripPressureTag() { return false; }

    default boolean canOutputPressurized(boolean consumePower) { return false; }
}
