package com.immersiveconvergence.api.client.split;

import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unused")
public final class SplitModelProperties {
    public static final IUnlistedProperty<BlockPos> SUBMODEL_OFFSET = new IUnlistedProperty<BlockPos>() {
        @Override public String getName() { return "ic_submodel_offset"; }

        @Override public boolean isValid(BlockPos value) { return true; }

        @Override public Class<BlockPos> getType() { return BlockPos.class; }

        @Override public String valueToString(BlockPos value) { return value.toString(); }
    };

    private SplitModelProperties() {}

    public static BlockStateContainer withOffset(Block block, BlockStateContainer container) {
        Collection<IProperty<?>> listed = container.getProperties();
        List<IUnlistedProperty<?>> unlisted = new ArrayList<>();
        if (container instanceof ExtendedBlockState) { unlisted.addAll(((ExtendedBlockState)container).getUnlistedProperties()); }
        unlisted.add(SUBMODEL_OFFSET);
        return new ExtendedBlockState(block, listed.toArray(new IProperty[0]), unlisted.toArray(new IUnlistedProperty[0]));
    }

    public static IBlockState withOffset(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (!(state instanceof IExtendedBlockState)) { return state; }
        BlockPos offset = modelOffset(world.getTileEntity(pos));
        return offset == null ? state : ((IExtendedBlockState)state).withProperty(SUBMODEL_OFFSET, offset);
    }

    @Nullable public static BlockPos modelOffset(@Nullable TileEntity te) {
        if (te instanceof ISubmodelOffsetProvider) { return ((ISubmodelOffsetProvider)te).getModelOffset(); }
        if (!(te instanceof TileEntityMultiblockPart) || !((TileEntityMultiblockPart<?>)te).formed) { return null; }
        int[] offset = ((TileEntityMultiblockPart<?>)te).offset;
        return new BlockPos(offset[0], offset[1], offset[2]);
    }
}
