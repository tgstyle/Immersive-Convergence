package com.immersiveconvergence.api.compat.computercraft;

import com.immersiveconvergence.api.compat.ICComputerTable;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ICPeripheralProvider implements IPeripheralProvider {
    private final List<ICComputerTable<?>> tables = new ArrayList<>();

    public void add(ICComputerTable<?> table) { tables.add(table); }

    @Nullable @Override public IPeripheral getPeripheral(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile == null) { return null; }
        for (ICComputerTable<?> table : tables) {
            IPeripheral peripheral = peripheral(world, tile, table);
            if (peripheral != null) { return peripheral; }
        }
        return null;
    }

    @Nullable private static <T extends TileEntity> IPeripheral peripheral(World world, TileEntity tile, ICComputerTable<T> table) {
        T target = table.locate(tile);
        return target == null ? null : new ICPeripheral<>(world, target.getPos(), table);
    }
}
