package com.immersiveconvergence.api.wires.fittings;

import javax.annotation.Nonnull;

public final class ICTransformerLayout {
    private final int cells;
    private final float depthMin;
    private final float depthMax;
    private final Terminal[] terminals;

    public ICTransformerLayout(int cells, float depthMin, float depthMax, @Nonnull Terminal... terminals) {
        this.cells = cells;
        this.depthMin = depthMin;
        this.depthMax = depthMax;
        this.terminals = terminals.clone();
    }

    public static Terminal terminal(double along, double height) { return new Terminal(along, height); }

    public int cells() { return cells; }

    public float depthMin() { return depthMin; }

    public float depthMax() { return depthMax; }

    public int terminalCount() { return terminals.length; }

    public double along(int port) { return terminals[port].along; }

    public double height(int port) { return terminals[port].height; }

    public static final class Terminal {
        private final double along;
        private final double height;

        private Terminal(double along, double height) {
            this.along = along;
            this.height = height;
        }
    }
}
