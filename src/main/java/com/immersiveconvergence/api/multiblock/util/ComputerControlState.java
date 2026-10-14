package com.immersiveconvergence.api.multiblock.util;

import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class ComputerControlState {
    private final AtomicInteger refCount = new AtomicInteger(0);
    private boolean isEnabled = true;

    public boolean isAttached() { return refCount.get() > 0; }

    public void setEnabled(boolean enabled) { isEnabled = enabled; }

    public void addReference() { refCount.incrementAndGet(); }

    public void removeReference() {
        if (refCount.decrementAndGet() <= 0) { clear(); }
    }

    public void clear() {
        refCount.set(0);
        isEnabled = true;
    }

    public void setOneRef() { refCount.set(1); }

    public boolean isEnabled() { return isEnabled; }
}
