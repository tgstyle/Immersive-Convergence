package com.immersiveconvergence.api.multiblock.util;

import com.google.common.base.Preconditions;

public class SetRestrictedField<T> {
    private T value;

    private SetRestrictedField() {}

    public static <T> SetRestrictedField<T> common() { return new SetRestrictedField<>(); }

    public void setValue(T value) { this.value = value; }

    public T getValue() { return Preconditions.checkNotNull(value); }
}
