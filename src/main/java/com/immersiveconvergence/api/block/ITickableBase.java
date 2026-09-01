package com.immersiveconvergence.api.block;

@SuppressWarnings({"unused", "RedundantSuppression"}) public interface ITickableBase {
    default boolean canTickAny() {
        return true;
    }
}
