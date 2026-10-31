package com.immersiveconvergence.api.wires.connectors;

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

public abstract class ICBlockTransformer extends ICBlockConnector {
    protected ICBlockTransformer(BlockContext context, String name, Supplier<? extends ICTileEntityTransformer> tile) { super(context, name, tile, ICProperties.MULTIBLOCKSLAVE); }

    @Override public boolean canITBlockBePlaced(World world, BlockPos pos, IBlockState newState, EnumFacing side, float hitX, float hitY, float hitZ, EntityPlayer player, ItemStack stack) {
        BlockPos middle = pos.up();
        BlockPos top = middle.up();
        return !world.isOutsideBuildHeight(top) && world.getBlockState(middle).getBlock().isReplaceable(world, middle) && world.getBlockState(top).getBlock().isReplaceable(world, top);
    }

    @Override public void onITBlockPlacedBy(World world, BlockPos pos, IBlockState state, EnumFacing side, float hitX, float hitY, float hitZ, EntityLivingBase placer, ItemStack stack) {
        super.onITBlockPlacedBy(world, pos, state, side, hitX, hitY, hitZ, placer, stack);
        TileEntity below = world.getTileEntity(pos);
        if (!(below instanceof ICTileEntityTransformer)) { return; }
        EnumFacing facing = ((ICTileEntityTransformer)below).getFacing();
        world.setBlockState(pos.up(), state);
        TileEntity middle = world.getTileEntity(pos.up());
        if (middle instanceof ICTileEntityTransformer) { ((ICTileEntityTransformer)middle).fill(facing); }
        world.setBlockState(pos.up(2), state);
        TileEntity top = world.getTileEntity(pos.up(2));
        if (top instanceof ICTileEntityTransformer) { ((ICTileEntityTransformer)top).raise(facing); }
    }

    @Override public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        boolean removeAbove = tile instanceof ICTileEntityTransformer && !((ICTileEntityTransformer)tile).isTop();
        boolean removeBelow = tile instanceof ICTileEntityTransformer && ((ICTileEntityTransformer)tile).isUpperCell();
        super.breakBlock(world, pos, state);
        if (removeAbove && world.getBlockState(pos.up()).getBlock() == this) { world.setBlockToAir(pos.up()); }
        if (removeBelow && world.getBlockState(pos.down()).getBlock() == this) { world.setBlockToAir(pos.down()); }
    }

    @Override @Nonnull public IBlockState getActualState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        IBlockState actual = super.getActualState(state, world, pos);
        TileEntity tile = world.getTileEntity(pos);
        return actual.withProperty(ICProperties.MULTIBLOCKSLAVE, tile instanceof ICTileEntityTransformer && ((ICTileEntityTransformer)tile).isUpperCell());
    }
}
