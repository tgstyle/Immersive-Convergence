package com.immersiveconvergence;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.bridge.ICBridges;
import com.immersiveconvergence.api.crafting.conditions.ICIEEnabledCondition;
import com.immersiveconvergence.api.integration.top.ProbeIntegration;
import com.immersiveconvergence.api.loot.LootEntryTypes;
import com.immersiveconvergence.api.multiblock.MultiblockRegistry;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.api.wires.ICWires;
import com.immersiveconvergence.common.bridge.ICBridgeService;
import com.immersiveconvergence.common.energy.wires.ICWireService;
import com.immersiveconvergence.core.config.ICClientConfig;
import com.immersiveconvergence.core.config.ICCommonConfig;
import com.immersiveconvergence.core.integration.computercraft.ICComputerCraft;
import com.immersiveconvergence.core.integration.immersiveengineering.IEClearTanks;
import com.immersiveconvergence.core.integration.immersiveengineering.IEWireBridge;
import com.immersiveconvergence.core.integration.immersivepetroleum.IPClearTanks;
import com.immersiveconvergence.core.integration.opencomputers.ICOpenComputers;
import com.immersiveconvergence.core.lib.ICLib;
import com.immersiveconvergence.core.network.PacketHandler;
import com.immersiveconvergence.core.proxy.ClientProxySupplier;
import com.immersiveconvergence.core.proxy.CommonProxy;
import com.immersiveconvergence.core.registration.ICBlockEntities;
import com.immersiveconvergence.core.registration.ICBlocks;
import com.immersiveconvergence.core.registration.ICCapabilities;
import com.immersiveconvergence.core.registration.ICCreativeTab;
import com.immersiveconvergence.core.registration.ICItems;
import com.immersiveconvergence.core.registration.ICMenuTypes;
import com.immersiveconvergence.core.registry.ICRegistryRemaps;
import com.immersiveconvergence.core.util.ICServerEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ICLib.MODID) public class ImmersiveConvergence {
    public static final CommonProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxySupplier::get, () -> CommonProxy::new);

    public ImmersiveConvergence(FMLJavaModLoadingContext context) {
        ICWires.install(new ICWireService());
        ICBridges.install(new ICBridgeService());
        IEventBus modEventBus = context.getModEventBus();
        CraftingHelper.register(ICIEEnabledCondition.Serializer.INSTANCE);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::loadComplete);
        modEventBus.addListener(this::enqueueIMC);
        modEventBus.addListener(ICCapabilities::register);
        if (ICMods.computerCraft()) { ICComputerCraft.init(); }
        ICLib.IC_LOGGER.info("Starting Proxy Mod Construction");
        CommonProxy.modConstruction();
        MultiblockRegistry.init();
        ICBlocks.init(modEventBus);
        ICItems.init(modEventBus);
        ICBlockEntities.init(modEventBus);
        ICMenuTypes.init(modEventBus);
        ICCreativeTab.init(modEventBus);
        LootEntryTypes.init(modEventBus);
        context.registerConfig(ModConfig.Type.COMMON, ICCommonConfig.SPEC);
        context.registerConfig(ModConfig.Type.CLIENT, ICClientConfig.SPEC);
        MinecraftForge.EVENT_BUS.register(ICServerEvents.class);
        MinecraftForge.EVENT_BUS.addListener(ICRegistryRemaps::handleRemapping);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ICLib.IC_LOGGER.info("HELLO FROM COMMON SETUP");
        event.enqueueWork(PacketHandler::initialize);
        if (ICMods.immersiveEngineering()) {
            IEClearTanks.register();
            event.enqueueWork(IEWireBridge::registerWireTypes);
        }
        if (ICMods.immersivePetroleum()) { IPClearTanks.register(); }
        if (ICMods.openComputers()) { event.enqueueWork(ICOpenComputers::register); }
    }

    private void loadComplete(final FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> {
            if (ICMods.immersiveEngineering()) { IEWireBridge.adoptWireTypes(); }
            ICWireType.freeze();
        });
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        if (ModList.get().isLoaded(ProbeIntegration.MODID)) { ProbeIntegration.enqueueIMC(); }
        if (ICMods.openComputers()) { ICOpenComputers.enqueueIMC(); }
    }

}
