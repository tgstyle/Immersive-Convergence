package com.immersiveconvergence.data;

import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid = ICLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT, Dist.DEDICATED_SERVER})
public class ICDataGenerators {
    @SubscribeEvent public static void generate(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput out = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();

        generator.addProvider(true, new ICBlockStates(out, helper));
        generator.addProvider(true, new ICDynamicModels(out, helper));
        generator.addProvider(true, new ICItemModels(out, helper));
        generator.addProvider(true, new ICLanguage(out));
        generator.addProvider(true, new LootTableProvider(out, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(ICBlockLoot::new, LootContextParamSets.BLOCK))));
    }
}
