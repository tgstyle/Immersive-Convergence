package com.immersiveconvergence.api.loot;

import com.immersiveconvergence.core.lib.ICLib;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class LootEntryTypes {
    private static final DeferredRegister<LootPoolEntryType> REGISTER = DeferredRegister.create(BuiltInRegistries.LOOT_POOL_ENTRY_TYPE.key(), ICLib.MODID);
    public static final DeferredHolder<LootPoolEntryType, LootPoolEntryType> DROP_INVENTORY = register("drop_inv", () -> InventoryDropLootEntry.CODEC);
    public static final DeferredHolder<LootPoolEntryType, LootPoolEntryType> BLOCK_ENTITY_DROP = register("tile_drop", () -> BlockEntityDropLootEntry.CODEC);

    public static void init(IEventBus bus) { REGISTER.register(bus); }

    private static DeferredHolder<LootPoolEntryType, LootPoolEntryType> register(String id, Supplier<MapCodec<? extends LootPoolEntryContainer>> codec) { return REGISTER.register(id, () -> new LootPoolEntryType(codec.get())); }
}
