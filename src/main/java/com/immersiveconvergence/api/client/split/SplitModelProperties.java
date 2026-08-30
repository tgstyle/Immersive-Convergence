package com.immersiveconvergence.api.client.split;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.property.IUnlistedProperty;

@SuppressWarnings("unused")
public final class SplitModelProperties {
    public static final IUnlistedProperty<BlockPos> SUBMODEL_OFFSET = new IUnlistedProperty<BlockPos>() {
        @Override public String getName() { return "ic_submodel_offset"; }

        @Override public boolean isValid(BlockPos value) { return true; }

        @Override public Class<BlockPos> getType() { return BlockPos.class; }

        @Override public String valueToString(BlockPos value) { return value.toString(); }
    };

    private SplitModelProperties() {}
}
