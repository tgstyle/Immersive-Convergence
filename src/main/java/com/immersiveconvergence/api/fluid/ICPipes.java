package com.immersiveconvergence.api.fluid;

public final class ICPipes {
    private static volatile int amountUnpressurized = IICFluidPipe.AMOUNT_UNPRESSURIZED;
    private static volatile int amountPressurized = IICFluidPipe.AMOUNT_PRESSURIZED;

    private ICPipes() {}

    public static void setTransferableAmounts(int unpressurized, int pressurized) {
        amountUnpressurized = unpressurized;
        amountPressurized = pressurized;
    }

    public static int getTransferableAmount(boolean pressurized) { return pressurized ? amountPressurized : amountUnpressurized; }
}
