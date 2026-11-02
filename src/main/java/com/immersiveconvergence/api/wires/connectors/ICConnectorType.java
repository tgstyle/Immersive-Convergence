package com.immersiveconvergence.api.wires.connectors;

import com.immersiveconvergence.api.block.ICBlockBase.IBlockEnum;

import javax.annotation.Nonnull;
import java.util.Locale;

@SuppressWarnings("unused") public enum ICConnectorType implements IBlockEnum {
    CONNECTOR;

    @Override public int getMeta() { return ordinal(); }

    @Override public boolean listForCreative() { return true; }

    @Override @Nonnull public String getName() { return name().toLowerCase(Locale.ROOT); }
}
