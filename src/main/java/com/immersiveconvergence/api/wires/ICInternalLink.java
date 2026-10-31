package com.immersiveconvergence.api.wires;

import javax.annotation.Nonnull;

public final class ICInternalLink {
    private final ICConnectionPoint first;
    private final ICConnectionPoint second;
    private final double loss;

    public ICInternalLink(@Nonnull ICConnectionPoint first, @Nonnull ICConnectionPoint second, double loss) {
        if (first.equals(second) || !first.position().equals(second.position())) { throw new IllegalArgumentException("An internal link joins two points of one block: " + first + " " + second); }
        boolean ordered = first.compareTo(second) < 0;
        this.first = ordered ? first : second;
        this.second = ordered ? second : first;
        this.loss = loss;
    }

    @Nonnull public ICConnectionPoint getFirst() { return first; }

    @Nonnull public ICConnectionPoint getSecond() { return second; }

    public double getLoss() { return loss; }

    @Override public boolean equals(Object other) {
        if (this == other) { return true; }
        if (!(other instanceof ICInternalLink link)) { return false; }
        return first.equals(link.first) && second.equals(link.second) && Double.compare(loss, link.loss) == 0;
    }

    @Override public int hashCode() { return 31 * (31 * first.hashCode() + second.hashCode()) + Double.hashCode(loss); }

    @Override public String toString() { return first + "=" + second + ":" + loss; }
}
