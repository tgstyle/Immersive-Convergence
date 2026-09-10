package com.immersiveconvergence.api;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public final class ICJEICatalysts {
    private static final Set<String> SUPPRESSED = Collections.synchronizedSet(new HashSet<>());

    private ICJEICatalysts() {}

    public static void suppress(String uniqueName) {
        if (uniqueName != null && !uniqueName.isEmpty()) { SUPPRESSED.add(uniqueName); }
    }

    public static void allow(String uniqueName) { SUPPRESSED.remove(uniqueName); }

    public static boolean suppressed(String uniqueName) { return uniqueName != null && SUPPRESSED.contains(uniqueName); }
}
