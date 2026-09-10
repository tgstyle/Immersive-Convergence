package com.immersiveconvergence.api.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;

public class IdenticalMergerI implements IIndexMerger {
    private final DoubleList coords;

    public IdenticalMergerI(DoubleList coords) { this.coords = coords; }

    @Override public void forMergedIndexes(IndexConsumer consumer) {
        int size = this.coords.size() - 1;
        for (int j = 0; j < size; j++) { if (!consumer.merge(j, j, j)) { return; } }
    }

    @Override public int size() { return this.coords.size(); }

    @Override public DoubleList getList() { return this.coords; }
}
