package com.immersiveconvergence.api.wires.fittings;

import com.immersiveconvergence.api.block.ICBlockBase.IBlockEnum;

import javax.annotation.Nonnull;
import java.util.Locale;

@SuppressWarnings("unused") public enum ICFittingType implements IBlockEnum {
    FITTING;

    @Override public int getMeta() { return ordinal(); }

    @Override public boolean listForCreative() { return true; }

    @Override @Nonnull public String getName() { return name().toLowerCase(Locale.ROOT); }
}
