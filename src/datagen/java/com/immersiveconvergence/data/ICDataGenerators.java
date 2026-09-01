package com.immersiveconvergence.data;

import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;

@EventBusSubscriber(modid = ICLib.MODID)
public class ICDataGenerators {
    @SubscribeEvent public static void generate(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();
        PackOutput out = generator.getPackOutput();
        final var lookup = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new LootTableProvider(out, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(ICBlockLoot::new, LootContextParamSets.BLOCK)), lookup));
        generator.addProvider(event.includeClient(), new ICBlockStates(out, helper));
        generator.addProvider(event.includeClient(), new ICDynamicModels(out, helper));
        generator.addProvider(event.includeClient(), new ICItemModels(out, helper));
        generator.addProvider(event.includeClient(), new ICLanguage(out));
    }
}
