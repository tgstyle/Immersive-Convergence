package com.immersiveconvergence.api.wires;

import com.immersiveconvergence.api.wires.connectors.ICTileEntityConnectable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IICWireService {
    @Nonnull IICWireNetwork network(@Nonnull World world);

    @Nonnull IICEnergyNetwork energy(@Nonnull World world);

    @Nonnull IICWireCollision collision();

    @Nonnull EnumActionResult useCoil(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumHand hand, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ);

    int connectionCount(@Nullable World world, @Nonnull BlockPos pos);

    void syncConnectionPoints(@Nonnull ICTileEntityConnectable tile);

    void connectableLoaded(@Nonnull ICTileEntityConnectable tile);

    void connectableInvalidated(@Nonnull ICTileEntityConnectable tile);
}
