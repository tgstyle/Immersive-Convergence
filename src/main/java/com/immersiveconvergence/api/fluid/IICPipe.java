package com.immersiveconvergence.api.fluid;

@SuppressWarnings("unused")
public interface IICPipe {
    boolean hasCover();

    void toggleSide(int side);

    int[] getSideConfig();
}
