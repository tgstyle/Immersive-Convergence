package com.immersiveconvergence.api.loot;

import com.immersiveconvergence.api.block.BlockInterfaces;

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

@SuppressWarnings({"unused", "RedundantSuppression"}) public class BlockEntityDropLootEntry extends LootPoolSingletonContainer {
    protected BlockEntityDropLootEntry(int weightIn, int qualityIn, LootItemCondition[] conditionsIn, LootItemFunction[] functionsIn) { super(weightIn, qualityIn, conditionsIn, functionsIn); }

    @Override protected void createItemStack(@Nonnull Consumer<ItemStack> output, LootContext context) {
        if (context.hasParam(LootContextParams.BLOCK_ENTITY)) {
            BlockEntity te = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
            if (te instanceof BlockInterfaces.IBlockEntityDrop drop) { drop.getBlockEntityDrop(context, output); }
        }
    }

    public static LootPoolSingletonContainer.Builder<?> builder() { return simpleBuilder(BlockEntityDropLootEntry::new); }

    @Nonnull @Override public LootPoolEntryType getType() { return LootEntryTypes.BLOCK_ENTITY_DROP.get(); }

    public static class Serializer extends LootPoolSingletonContainer.Serializer<BlockEntityDropLootEntry> {
        @Nonnull @Override protected BlockEntityDropLootEntry deserialize(@Nonnull JsonObject json, @Nonnull JsonDeserializationContext context, int weight, int quality, @Nonnull LootItemCondition[] conditions, @Nonnull LootItemFunction[] functions) { return new BlockEntityDropLootEntry(weight, quality, conditions, functions); }
    }
}
