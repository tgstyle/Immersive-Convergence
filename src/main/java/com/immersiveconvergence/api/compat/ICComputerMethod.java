package com.immersiveconvergence.api.compat;

@FunctionalInterface public interface ICComputerMethod<T> {
    Object[] call(T object, Object[] args);
}
