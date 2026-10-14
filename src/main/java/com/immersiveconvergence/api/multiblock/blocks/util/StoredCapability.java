package com.immersiveconvergence.api.multiblock.blocks.util;

import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockContext;

import net.minecraftforge.common.util.LazyOptional;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class StoredCapability<T> {
    private final T value;
    private LazyOptional<T> cap = LazyOptional.empty();

    public StoredCapability(T value) { this.value = value; }

    public LazyOptional<T> get(IMultiblockContext<?> ctx) {
        if (cap.isPresent()) { return cap; }
        final LazyOptional<T> result = ctx.registerCapability(value);
        this.cap = result;
        return result;
    }

    public <U> LazyOptional<U> cast(IMultiblockContext<?> ctx) { return get(ctx).cast(); }

    public T getValue() { return value; }
}
