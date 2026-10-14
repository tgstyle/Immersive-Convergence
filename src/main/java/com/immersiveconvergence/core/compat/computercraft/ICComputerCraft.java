package com.immersiveconvergence.core.compat.computercraft;

import com.immersiveconvergence.api.compat.ICComputerTable;
import com.immersiveconvergence.api.compat.ICComputerTables;
import com.immersiveconvergence.api.compat.computercraft.ICPeripheral;

import dan200.computercraft.api.peripheral.PeripheralCapability;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.util.function.Supplier;

public final class ICComputerCraft {
    private ICComputerCraft() {}

    public static void init(IEventBus modBus) { modBus.addListener(ICComputerCraft::registerCapabilities); }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) { for (ICComputerTable<?> table : ICComputerTables.all()) { register(event, table); } }

    private static <T> void register(RegisterCapabilitiesEvent event, ICComputerTable<T> table) { for (Supplier<? extends BlockEntityType<?>> type : table.blockEntities()) { register(event, type.get(), table); } }

    private static <B extends BlockEntity, T> void register(RegisterCapabilitiesEvent event, BlockEntityType<B> type, ICComputerTable<T> table) { event.registerBlockEntity(PeripheralCapability.get(), type, (blockEntity, side) -> table.attachesTo(blockEntity) ? new ICPeripheral<>(blockEntity, table) : null); }
}
