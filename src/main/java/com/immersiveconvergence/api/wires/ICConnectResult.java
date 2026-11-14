package com.immersiveconvergence.api.wires;

import javax.annotation.Nullable;

public enum ICConnectResult {
    OK(null),
    NO_FITTING("invalidPoint"),
    WRONG_TYPE("wrongCable"),
    TOO_LONG("tooFar"),
    OBSTRUCTED("obstructed"),
    DUPLICATE("duplicate"),
    SELF("sameFitting"),
    UNLOADED("unloaded"),
    CANCELED("canceled");

    private final String key;

    ICConnectResult(@Nullable String key) { this.key = key; }

    @Nullable public String key() { return key; }
}
