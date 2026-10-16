package com.immersiveconvergence.api.multiblock.util;

import com.google.common.base.Preconditions;
import java.util.function.Supplier;

public class SetRestrictedField<T> implements Supplier<T> {
    private T value;

    private SetRestrictedField() {}

    public static <T> SetRestrictedField<T> common() { return new SetRestrictedField<>(); }

    public void setValue(T value) { this.value = value; }

    @Override public T get() { return Preconditions.checkNotNull(value); }
}
