package com.immersiveconvergence.common.blocks.types;

import com.immersiveconvergence.api.block.ICBlockBase;

import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;
import java.util.Locale;

public enum ICBlockType_Device implements IStringSerializable, ICBlockBase.IBlockEnum {
    ROTOR_CREATIVE,
    HEAT_CREATIVE;

    @Override @Nonnull public String getName() { return this.toString().toLowerCase(Locale.ENGLISH); }

    @Override public int getMeta() { return ordinal(); }

    @Override public boolean listForCreative() { return true; }
}
