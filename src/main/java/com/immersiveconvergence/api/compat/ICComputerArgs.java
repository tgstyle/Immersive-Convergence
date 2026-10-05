package com.immersiveconvergence.api.compat;

public final class ICComputerArgs {
    private ICComputerArgs() {}

    public static boolean checkBoolean(Object[] args, int index) {
        Object value = index < args.length ? args[index] : null;
        if (value instanceof Boolean) { return (Boolean)value; }
        throw badArgument(index, "boolean", value);
    }

    public static int checkInteger(Object[] args, int index) {
        Object value = index < args.length ? args[index] : null;
        if (value instanceof Number) { return ((Number)value).intValue(); }
        throw badArgument(index, "number", value);
    }

    public static int checkRange(Object[] args, int index, int max, String message) {
        int value = checkInteger(args, index);
        if (value < 1 || value > max) { throw new IllegalArgumentException(message); }
        return value - 1;
    }

    private static IllegalArgumentException badArgument(int index, String expected, Object value) {
        String actual = value == null ? "no value" : value instanceof Number ? "number" : value instanceof String ? "string" : value instanceof Boolean ? "boolean" : "table";
        return new IllegalArgumentException("bad argument #" + (index + 1) + " (" + expected + " expected, got " + actual + ")");
    }
}
