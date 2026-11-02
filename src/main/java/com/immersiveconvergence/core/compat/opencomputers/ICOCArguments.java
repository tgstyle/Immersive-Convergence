package com.immersiveconvergence.core.compat.opencomputers;

import li.cil.oc.api.machine.Arguments;
import java.nio.charset.StandardCharsets;

public final class ICOCArguments {
    private ICOCArguments() {}

    public static Object[] normalize(Arguments args) {
        Object[] raw = args.toArray();
        Object[] result = new Object[raw.length];
        for (int i = 0; i < raw.length; i++) { result[i] = normalize(raw[i]); }
        return result;
    }

    private static Object normalize(Object value) {
        if (value instanceof Number number) { return number.doubleValue(); }
        if (value instanceof byte[] bytes) { return new String(bytes, StandardCharsets.UTF_8); }
        return value;
    }
}
