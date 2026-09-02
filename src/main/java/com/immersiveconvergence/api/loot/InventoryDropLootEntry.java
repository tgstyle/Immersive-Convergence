package com.immersiveconvergence.api.loot;

import com.immersiveconvergence.api.block.BlockInterfaces;
import com.immersiveconvergence.api.util.IItemDropProvider;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class InventoryDropLootEntry extends LootPoolSingletonContainer {
    protected InventoryDropLootEntry(int weightIn, int qualityIn, LootItemCondition[] conditionsIn, LootItemFunction[] functionsIn) { super(weightIn, qualityIn, conditionsIn, functionsIn); }

    @Override protected void createItemStack(@Nonnull Consumer<ItemStack> output, LootContext context) {
        if (context.hasParam(LootContextParams.BLOCK_ENTITY)) {
            BlockEntity te = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
            if (te instanceof BlockInterfaces.IGeneralMultiblock dummyBE) { te = (BlockEntity) dummyBE.master(); }
            if (te instanceof IItemDropProvider dropProvider && dropProvider.getDroppedItems() != null) { dropProvider.getDroppedItems().forEach(output); }
        }
    }

    public static LootPoolSingletonContainer.Builder<?> builder() { return simpleBuilder(InventoryDropLootEntry::new); }

    @Nonnull @Override public LootPoolEntryType getType() { return LootEntryTypes.DROP_INVENTORY.get(); }

    public static class Serializer extends LootPoolSingletonContainer.Serializer<InventoryDropLootEntry> {
        @Nonnull @Override protected InventoryDropLootEntry deserialize(@Nonnull JsonObject json, @Nonnull JsonDeserializationContext context, int weight, int quality, @Nonnull LootItemCondition[] conditions, @Nonnull LootItemFunction[] functions) { return new InventoryDropLootEntry(weight, quality, conditions, functions); }
    }
}
