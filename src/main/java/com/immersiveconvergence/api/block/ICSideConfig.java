package com.immersiveconvergence.api.block;

import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;
import java.util.Locale;

@SuppressWarnings("unused")
public enum ICSideConfig implements IStringSerializable {
    NONE("none"),
    INPUT("in"),
    OUTPUT("out");

    private final String texture;

    ICSideConfig(String texture) { this.texture = texture; }

    @Override @Nonnull public String getName() { return this.toString().toLowerCase(Locale.ENGLISH); }

    public String getTextureName() { return texture; }

    public static ICSideConfig next(ICSideConfig current) { return current == INPUT ? OUTPUT : current == OUTPUT ? NONE : INPUT; }
}
