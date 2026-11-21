package com.immersiveconvergence.common.blocks.types;

import com.immersiveconvergence.api.block.ICBlockBase;

import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;
import java.util.Locale;

public enum ICBlockType_CreativeEnergySource implements IStringSerializable, ICBlockBase.IBlockEnum {
    SOURCE;

    @Override @Nonnull public String getName() { return this.toString().toLowerCase(Locale.ENGLISH); }

    @Override public int getMeta() { return ordinal(); }

    @Override public boolean listForCreative() { return true; }
}
