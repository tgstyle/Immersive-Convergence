package com.immersiveconvergence.api.block;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.IEProperties.ProperySideConfig;
import blusunrize.immersiveengineering.api.IEProperties.PropertyBoolInverted;
import blusunrize.immersiveengineering.api.IEProperties.PropertySet;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.HashMap;

@SuppressWarnings("unused")
public class ICProperties {
    public static final PropertyDirection FACING_ALL = IEProperties.FACING_ALL;
    public static final PropertyDirection FACING_HORIZONTAL = IEProperties.FACING_HORIZONTAL;
    public static final PropertyBoolInverted MULTIBLOCKSLAVE = IEProperties.MULTIBLOCKSLAVE;
    public static final PropertyBoolInverted DYNAMICRENDER = IEProperties.DYNAMICRENDER;
    public static final PropertySet CONNECTIONS = IEProperties.CONNECTIONS;
    public static final ProperySideConfig[] SIDECONFIG = IEProperties.SIDECONFIG;
    public static final PropertyBoolInverted[] BOOLEANS = IEProperties.BOOLEANS;
    @SuppressWarnings("rawtypes") public static final IUnlistedProperty<HashMap> OBJ_TEXTURE_REMAP = IEProperties.OBJ_TEXTURE_REMAP;
    public static final IUnlistedProperty<TileEntity> TILEENTITY_PASSTHROUGH = IEProperties.TILEENTITY_PASSTHROUGH;
}
