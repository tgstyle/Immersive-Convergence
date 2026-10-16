package com.immersiveconvergence.api.block;

public interface ITickableBase {
    default boolean canTickAny() { return true; }
}
