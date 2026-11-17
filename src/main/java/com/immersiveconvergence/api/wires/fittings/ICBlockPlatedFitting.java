package com.immersiveconvergence.api.wires.fittings;

import com.immersiveconvergence.api.block.BlockContext;

import com.google.common.collect.Iterators;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.Models;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.Properties;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class ICBlockPlatedFitting extends ICBlockFitting {
    private static final IModelState HIDE_PLATE = part -> part.isPresent() && Iterators.contains(Models.getParts(part.get()), "cube_plate") ? Optional.of(TRSRTransformation.identity()) : Optional.empty();

    protected ICBlockPlatedFitting(BlockContext context, String name, Supplier<? extends ICTileEntityFitting> tile) { super(context, name, tile, Properties.AnimationProperty); }

    @Override @Nonnull public IBlockState getExtendedState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        IBlockState extended = super.getExtendedState(state, world, pos);
        TileEntity tile = world.getTileEntity(pos);
        if (!(extended instanceof IExtendedBlockState) || !(tile instanceof ICTileEntityFitting) || ((ICTileEntityFitting)tile).plateOn()) { return extended; }
        return ((IExtendedBlockState)extended).withProperty(Properties.AnimationProperty, HIDE_PLATE);
    }
}
