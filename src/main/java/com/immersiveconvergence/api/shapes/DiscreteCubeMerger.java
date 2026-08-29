package com.immersiveconvergence.api.shapes;

import com.immersiveconvergence.common.util.ICMth;

import it.unimi.dsi.fastutil.doubles.DoubleList;

public final class DiscreteCubeMerger implements IndexMerger {
    private final CubePointRange result;
    private final int firstDiv, secondDiv;

    public DiscreteCubeMerger(int aa, int bb) {
        this.result = new CubePointRange((int) ICMth.lcm(aa, bb));
        int gcd = ICMth.gcd(aa, bb);
        this.firstDiv = aa / gcd;
        this.secondDiv = bb / gcd;
    }

    public void forMergedIndexes(IndexConsumer consumer) {
        int size = this.result.size() - 1;
        for (int j = 0; j < size; j++) { if (!consumer.merge(j / this.secondDiv, j / this.firstDiv, j)) { return; } }
    }

    public int size() { return this.result.size(); }

    public DoubleList getList() { return this.result; }
}
