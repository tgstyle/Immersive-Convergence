package com.immersiveconvergence.client;

public final class ICManualHighlight {
    public static final float RED = 1f;
    public static final float GREEN = 0.45f;
    public static final float BLUE = 0.45f;
    public static final float VOLUME_ALPHA = 0.3f;
    public static final float OVERWRITE_BRIGHTNESS = 0.75f;

    private static boolean active;

    private ICManualHighlight() {}

    public static void set(boolean value) { active = value; }

    public static boolean isActive() { return active; }
}
