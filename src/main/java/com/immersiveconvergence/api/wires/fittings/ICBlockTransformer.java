package com.immersiveconvergence.api.wires.fittings;

import com.immersiveconvergence.api.block.BlockContext;
import com.immersiveconvergence.api.block.ICProperties;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public abstract class ICBlockTransformer extends ICBlockFitting {
    private final ICTransformerLayout layout;

    protected ICBlockTransformer(BlockContext context, String name, Supplier<? extends ICTileEntityTransformer> tile, ICTransformerLayout layout) {
        super(context, name, tile, ICProperties.MULTIBLOCKSLAVE);
        this.layout = layout;
    }

    @Override public boolean canITBlockBePlaced(World world, BlockPos pos, IBlockState newState, EnumFacing side, float hitX, float hitY, float hitZ, EntityPlayer player, ItemStack stack) {
        for (int index = 1; index < layout.cells(); index++) {
            BlockPos above = pos.up(index);
            if (world.isOutsideBuildHeight(above) || !world.getBlockState(above).getBlock().isReplaceable(world, above)) { return false; }
        }
        return true;
    }

    @Override public void onITBlockPlacedBy(World world, BlockPos pos, IBlockState state, EnumFacing side, float hitX, float hitY, float hitZ, EntityLivingBase placer, ItemStack stack) {
        super.onITBlockPlacedBy(world, pos, state, side, hitX, hitY, hitZ, placer, stack);
        TileEntity base = world.getTileEntity(pos);
        if (!(base instanceof ICTileEntityTransformer)) { return; }
        EnumFacing facing = ((ICTileEntityTransformer)base).getFacing();
        ((ICTileEntityTransformer)base).assemble(facing, 0);
        for (int index = 1; index < layout.cells(); index++) {
            BlockPos above = pos.up(index);
            world.setBlockState(above, state);
            TileEntity tile = world.getTileEntity(above);
            if (tile instanceof ICTileEntityTransformer) { ((ICTileEntityTransformer)tile).assemble(facing, index); }
        }
    }

    @Override public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        boolean cascade = tile instanceof ICTileEntityTransformer;
        int cell = cascade ? ((ICTileEntityTransformer)tile).cell() : 0;
        super.breakBlock(world, pos, state);
        if (!cascade) { return; }
        BlockPos base = pos.down(cell);
        for (int index = 0; index < layout.cells(); index++) {
            BlockPos other = base.up(index);
            if (index != cell && world.getBlockState(other).getBlock() == this) { world.setBlockToAir(other); }
        }
    }

    @Override @Nonnull public IBlockState getActualState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        IBlockState actual = super.getActualState(state, world, pos);
        TileEntity tile = world.getTileEntity(pos);
        return actual.withProperty(ICProperties.MULTIBLOCKSLAVE, tile instanceof ICTileEntityTransformer && ((ICTileEntityTransformer)tile).isUpperCell());
    }
}
