package com.immersiveconvergence.api.multiblock.util;

import java.util.concurrent.atomic.AtomicInteger;

public class ComputerControlState {
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

    public boolean isEnabled() { return isEnabled; }
}
