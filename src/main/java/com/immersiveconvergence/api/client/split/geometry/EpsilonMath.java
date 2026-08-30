package com.immersiveconvergence.api.client.split.geometry;

public final class EpsilonMath {
    public static final EpsilonMath DEFAULT = new EpsilonMath(1.0E-5D);
    private final double epsilon;

    public EpsilonMath(double epsilon) { this.epsilon = epsilon; }

    public Sign sign(double value) {
        if (value < -this.epsilon) { return Sign.NEGATIVE; }
        else { return value > this.epsilon ? Sign.POSITIVE : Sign.ZERO; }
    }

    public int floor(double in) { return (int)Math.floor(in + this.epsilon); }

    public int ceil(double in) { return (int)Math.ceil(in - this.epsilon); }

    public enum Sign {
        POSITIVE, ZERO, NEGATIVE;

        public Sign invert() {
            switch (this) {
                case POSITIVE: return NEGATIVE;
                case NEGATIVE: return POSITIVE;
                default: return ZERO;
            }
        }
    }
}
