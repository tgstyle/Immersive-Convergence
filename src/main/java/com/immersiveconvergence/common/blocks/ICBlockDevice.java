package com.immersiveconvergence.common.blocks;

import com.immersiveconvergence.api.block.ICBlockTileProvider;
import com.immersiveconvergence.api.block.ICProperties;
import com.immersiveconvergence.api.block.ICItemBlockBase;
import com.immersiveconvergence.common.ICBlockContext;
import com.immersiveconvergence.common.blocks.tileentities.TileEntityHeatCreative;
import com.immersiveconvergence.common.blocks.tileentities.TileEntityRotorCreative;
import com.immersiveconvergence.common.blocks.types.ICBlockType_Device;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ICBlockDevice extends ICBlockTileProvider<ICBlockType_Device> {

    public ICBlockDevice() {
        super(ICBlockContext.CONTEXT, "device", Material.IRON, PropertyEnum.create("type", ICBlockType_Device.class), ICItemBlockBase.class, ICProperties.FACING_ALL, ICProperties.MULTIBLOCKSLAVE, ICProperties.BOOLEANS[0], ICProperties.DYNAMICRENDER, ICProperties.TILEENTITY_PASSTHROUGH);
        this.setHardness(3.0F);
        this.setResistance(15.0F);
        lightOpacity = 0;
        this.setAllNotNormalBlock();
    }

    @Override public boolean useCustomStateMapper() { return true; }

    @Override @Nonnull public String getCustomStateMapping(int meta, boolean itemBlock) { return ICBlockType_Device.values()[meta].getName(); }

    @Override public boolean allowHammerHarvest(IBlockState state) { return true; }

    @Override public TileEntity createBasicTE(World worldIn, ICBlockType_Device type) {
        switch (type) {
            case ROTOR_CREATIVE: { return new TileEntityRotorCreative(); }
            case HEAT_CREATIVE: { return new TileEntityHeatCreative(); }
        }
        return null;
    }
}
