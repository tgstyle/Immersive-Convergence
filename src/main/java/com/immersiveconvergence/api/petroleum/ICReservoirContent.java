package com.immersiveconvergence.api.petroleum;

public enum ICReservoirContent {
    LIQUID,
    GAS,
    EMPTY,
    DEFAULT;

    public static ICReservoirContent byName(String name) {
        for (ICReservoirContent content : values()) {
            if (content.name().equals(name)) { return content; }
        }
        return DEFAULT;
    }
}
