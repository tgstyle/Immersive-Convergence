package com.immersiveconvergence.api.compat.opencomputers;

import com.immersiveconvergence.api.compat.ICComputerTable;

import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.BiFunction;

public class ICTableDriver<T extends TileEntity> extends DriverSidedTileEntity {
    private final ICComputerTable<T> table;
    private final BiFunction<World, BlockPos, ManagedEnvironment> environment;

    public ICTableDriver(ICComputerTable<T> table, BiFunction<World, BlockPos, ManagedEnvironment> environment) {
        this.table = table;
        this.environment = environment;
    }

    @Override public ManagedEnvironment createEnvironment(World world, BlockPos pos, EnumFacing facing) {
        T target = table.locate(world.getTileEntity(pos));
        return target == null ? null : environment.apply(world, target.getPos());
    }

    @Override public Class<?> getTileEntityClass() { return table.tileClass; }
}
