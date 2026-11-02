package com.immersiveconvergence.core.compat.opencomputers;

import com.immersiveconvergence.api.compat.ICComputerTable;
import com.immersiveconvergence.api.compat.ICComputerTables;

import li.cil.oc.api.driver.DriverBlock;
import li.cil.oc.api.network.ManagedEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public final class ICTableBlockDriver implements DriverBlock {
    private final Map<BlockEntityType<?>, ICComputerTable<?>> tables = new HashMap<>();

    public ICTableBlockDriver() {
        for (ICComputerTable<?> table : ICComputerTables.all()) {
            for (Supplier<? extends BlockEntityType<?>> type : table.blockEntities()) { tables.put(type.get(), table); }
        }
    }

    @Override public boolean worksWith(Level level, BlockPos pos, Direction side) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) { return false; }
        ICComputerTable<?> table = tables.get(blockEntity.getType());
        return table != null && table.attachesTo(blockEntity);
    }

    @Override public ManagedEnvironment createEnvironment(Level level, BlockPos pos, Direction side) {
        BlockEntity blockEntity = Objects.requireNonNull(level.getBlockEntity(pos));
        return environment(blockEntity, tables.get(blockEntity.getType()));
    }

    private static <T> ManagedEnvironment environment(BlockEntity blockEntity, ICComputerTable<T> table) { return new ICTableEnvironment<>(blockEntity, table); }
}
