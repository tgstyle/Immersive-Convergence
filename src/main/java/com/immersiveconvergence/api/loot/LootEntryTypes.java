package com.immersiveconvergence.api.loot;

import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class LootEntryTypes {
    private static final DeferredRegister<LootPoolEntryType> REGISTER = DeferredRegister.create(BuiltInRegistries.LOOT_POOL_ENTRY_TYPE.key(), ICLib.MODID);
    public static final RegistryObject<LootPoolEntryType> DROP_INVENTORY = register("drop_inv", InventoryDropLootEntry.Serializer::new);
    public static final RegistryObject<LootPoolEntryType> BLOCK_ENTITY_DROP = register("tile_drop", BlockEntityDropLootEntry.Serializer::new);

    public static void init(IEventBus bus) { REGISTER.register(bus); }

    private static RegistryObject<LootPoolEntryType> register(String id, Supplier<Serializer<? extends LootPoolEntryContainer>> serializer) { return REGISTER.register(id, () -> new LootPoolEntryType(serializer.get())); }
}
