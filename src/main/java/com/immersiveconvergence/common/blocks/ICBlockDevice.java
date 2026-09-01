package com.immersiveconvergence.common.blocks;

import com.immersiveconvergence.api.block.ICBlockTileProvider;
import com.immersiveconvergence.api.block.ICItemBlockBase;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces;
import com.immersiveconvergence.common.ICBlockContext;
import com.immersiveconvergence.common.blocks.tileentities.TileEntityHeatCreative;
import com.immersiveconvergence.common.blocks.tileentities.TileEntityRotorCreative;
import com.immersiveconvergence.common.blocks.types.ICBlockType_Device;

import blusunrize.immersiveengineering.api.IEProperties;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ICBlockDevice extends ICBlockTileProvider<ICBlockType_Device> {

    public ICBlockDevice() {
        super(ICBlockContext.CONTEXT, "device", Material.IRON, PropertyEnum.create("type", ICBlockType_Device.class), ICItemBlockBase.class, IEProperties.FACING_ALL, IEProperties.MULTIBLOCKSLAVE, IEProperties.BOOLEANS[0], IEProperties.DYNAMICRENDER, IEProperties.TILEENTITY_PASSTHROUGH);
        this.setHardness(3.0F);
        this.setResistance(15.0F);
        lightOpacity = 0;
        this.setAllNotNormalBlock();
    }

    @Override public boolean useCustomStateMapper() { return true; }

    @Override @Nonnull public String getCustomStateMapping(int meta, boolean itemBlock) { return ICBlockType_Device.values()[meta].getName(); }

    @Override public boolean allowHammerHarvest(IBlockState state) { return true; }

    @Override @Nonnull public AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos) {
        TileEntity te = source.getTileEntity(pos);
        if (te instanceof ICBlockInterfaces.IBlockBounds) {
            float[] bounds = ((ICBlockInterfaces.IBlockBounds)te).getBlockBounds();
            return new AxisAlignedBB(bounds[0], bounds[1], bounds[2], bounds[3], bounds[4], bounds[5]);
        }
        return super.getBoundingBox(state, source, pos);
    }

    @Override public TileEntity createBasicTE(World worldIn, ICBlockType_Device type) {
        switch (type) {
            case ROTOR_CREATIVE: { return new TileEntityRotorCreative(); }
            case HEAT_CREATIVE: { return new TileEntityHeatCreative(); }
        }
        return null;
    }
}
