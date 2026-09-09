package com.immersiveconvergence.api.manual;

import com.immersiveconvergence.api.multiblock.ICMultiblock;

import javax.annotation.Nullable;

public final class ICManualPage {
    public enum Kind { TEXT, MULTIBLOCK, CRAFTING, IMAGE }

    private final Kind kind;
    private final String key;
    private final ICMultiblock multiblock;
    private final Object[] arguments;

    private ICManualPage(Kind kind, String key, @Nullable ICMultiblock multiblock, Object... arguments) {
        this.kind = kind;
        this.key = key;
        this.multiblock = multiblock;
        this.arguments = arguments;
    }

    public static ICManualPage text(String key) { return new ICManualPage(Kind.TEXT, key, null); }

    public static ICManualPage multiblock(String key, ICMultiblock multiblock) { return new ICManualPage(Kind.MULTIBLOCK, key, multiblock); }

    public static ICManualPage crafting(String key, Object... stacks) { return new ICManualPage(Kind.CRAFTING, key, null, stacks); }

    public static ICManualPage image(String key, String... images) { return new ICManualPage(Kind.IMAGE, key, null, (Object[])images); }

    public Kind kind() { return kind; }

    public String key() { return key; }

    @Nullable public ICMultiblock multiblock() { return multiblock; }

    public Object[] arguments() { return arguments; }
}
