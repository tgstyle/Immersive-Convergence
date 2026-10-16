package com.immersiveconvergence.api.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ResettableLazy<T> implements Supplier<T> {
    private final Supplier<T> getter;
    @Nullable private T cached;

    public ResettableLazy(Supplier<T> getter) { this.getter = getter; }

    @Nonnull @Override public T get() {
        if (cached == null) { cached = getter.get(); }
        return cached;
    }

    public void reset() { cached = null; }
}
