package com.immersiveconvergence.api.compat;

public final class ICComputerArgs {
    private ICComputerArgs() {}

    public static boolean checkBoolean(Object[] args, int index) {
        Object value = index < args.length ? args[index] : null;
        if (value instanceof Boolean bool) { return bool; }
        String actual = value == null ? "no value" : value instanceof Number ? "number" : value instanceof String ? "string" : "table";
        throw new IllegalArgumentException("bad argument #" + (index + 1) + " (boolean expected, got " + actual + ")");
    }
}
