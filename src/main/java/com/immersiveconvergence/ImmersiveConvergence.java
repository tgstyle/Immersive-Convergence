package com.immersiveconvergence;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.crafting.conditions.ICIEEnabledCondition;
import com.immersiveconvergence.api.integration.top.ProbeIntegration;
import com.immersiveconvergence.api.loot.LootEntryTypes;
import com.immersiveconvergence.api.multiblock.MultiblockRegistry;
import com.immersiveconvergence.core.compat.IEClearTanks;
import com.immersiveconvergence.core.compat.IPClearTanks;
import com.immersiveconvergence.core.compat.computercraft.ICComputerCraft;
import com.immersiveconvergence.core.ICClientConfig;
import com.immersiveconvergence.core.ICCommonConfig;
import com.immersiveconvergence.core.lib.ICLib;
import com.immersiveconvergence.core.registration.ICBlockEntities;
import com.immersiveconvergence.core.registration.ICBlocks;
import com.immersiveconvergence.core.registration.ICCreativeTab;
import com.immersiveconvergence.core.registration.ICItems;
import com.immersiveconvergence.core.registration.ICMenuTypes;
import com.immersiveconvergence.core.registry.ICRegistryAliases;
import com.immersiveconvergence.core.proxy.CommonProxy;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(ICLib.MODID) public class ImmersiveConvergence {
    public ImmersiveConvergence(IEventBus modEventBus, ModContainer container) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::enqueueIMC);
        modEventBus.addListener(ICBlockEntities::registerCapabilities);
        if (ICMods.computerCraft()) { ICComputerCraft.init(modEventBus); }
        CommonProxy.modConstruction();
        MultiblockRegistry.init(modEventBus);
        ICBlocks.init(modEventBus);
        ICItems.init(modEventBus);
        ICBlockEntities.init(modEventBus);
        ICMenuTypes.init(modEventBus);
        ICCreativeTab.init(modEventBus);
        LootEntryTypes.init(modEventBus);
        ICIEEnabledCondition.init(modEventBus);
        container.registerConfig(ModConfig.Type.COMMON, ICCommonConfig.SPEC);
        container.registerConfig(ModConfig.Type.CLIENT, ICClientConfig.SPEC);
        NeoForge.EVENT_BUS.register(ImmersiveConvergence.class);
        ICRegistryAliases.register();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        if (ICMods.immersiveEngineering()) { IEClearTanks.register(); }
        if (ICMods.immersivePetroleum()) { IPClearTanks.register(); }
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        if (ModList.get().isLoaded(ProbeIntegration.MODID)) { ProbeIntegration.enqueueIMC(); }
    }

    @SubscribeEvent public static void onServerStarting(ServerStartingEvent event) {}
}