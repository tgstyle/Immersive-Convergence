package com.immersiveconvergence;

import com.immersiveconvergence.api.capability.*;
import com.immersiveconvergence.api.integration.top.ProbeIntegration;
import com.immersiveconvergence.api.loot.LootEntryTypes;
import com.immersiveconvergence.core.ICClientConfig;
import com.immersiveconvergence.core.ICCommonConfig;
import com.immersiveconvergence.core.lib.ICLib;
import com.immersiveconvergence.core.network.PacketHandler;
import com.immersiveconvergence.core.registry.ICRegistryRemaps;
import com.immersiveconvergence.core.registration.ICBlockEntities;
import com.immersiveconvergence.core.registration.ICBlocks;
import com.immersiveconvergence.core.registration.ICCreativeTab;
import com.immersiveconvergence.core.registration.ICItems;
import com.immersiveconvergence.core.registration.ICMenuTypes;
import com.immersiveconvergence.core.proxy.ClientProxySupplier;
import com.immersiveconvergence.core.proxy.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@SuppressWarnings("unused")
@Mod(ICLib.MODID)
public class ImmersiveConvergence {
    public static final CommonProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxySupplier::get, () -> CommonProxy::new);

    public ImmersiveConvergence(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::enqueueIMC);
        modEventBus.addListener(this::registerCapabilities);
        ICLib.IC_LOGGER.info("Starting Proxy Mod Construction");
        CommonProxy.modConstruction(modEventBus);
        ICBlocks.init(modEventBus);
        ICItems.init(modEventBus);
        ICBlockEntities.init(modEventBus);
        ICMenuTypes.init(modEventBus);
        ICCreativeTab.init(modEventBus);
        LootEntryTypes.init(modEventBus);
        context.registerConfig(ModConfig.Type.COMMON, ICCommonConfig.SPEC);
        context.registerConfig(ModConfig.Type.CLIENT, ICClientConfig.SPEC);
        MinecraftForge.EVENT_BUS.register(ImmersiveConvergence.class);
        MinecraftForge.EVENT_BUS.addListener(ICRegistryRemaps::handleRemapping);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ICLib.IC_LOGGER.info("HELLO FROM COMMON SETUP");
        event.enqueueWork(PacketHandler::initialize);
    }

    private void enqueueIMC(final InterModEnqueueEvent event) { ProbeIntegration.enqueueIMC(); }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IHeatProvider.class);
        event.register(IHeatConsumer.class);
        event.register(IMechanicalEnergyProvider.class);
        event.register(IMechanicalEnergyConsumer.class);
        HeatCapabilities.HEAT_PROVIDER_CAPABILITY = CapabilityManager.get(HEAT_PROVIDER_TOKEN);
        HeatCapabilities.HEAT_CONSUMER_CAPABILITY = CapabilityManager.get(HEAT_CONSUMER_TOKEN);
        MechanicalCapabilities.MECHANICAL_PROVIDER_CAPABILITY = CapabilityManager.get(MECHANICAL_PROVIDER_TOKEN);
        MechanicalCapabilities.MECHANICAL_CONSUMER_CAPABILITY = CapabilityManager.get(MECHANICAL_CONSUMER_TOKEN);
        RadiationCapabilities.RADIATION_PROVIDER_CAPABILITY = CapabilityManager.get(RADIATION_PROVIDER_TOKEN);
        RadiationCapabilities.RADIATION_CONSUMER_CAPABILITY = CapabilityManager.get(RADIATION_CONSUMER_TOKEN);
    }

    private static final CapabilityToken<IHeatProvider> HEAT_PROVIDER_TOKEN = new CapabilityToken<>() {};
    private static final CapabilityToken<IHeatConsumer> HEAT_CONSUMER_TOKEN = new CapabilityToken<>() {};
    private static final CapabilityToken<IMechanicalEnergyProvider> MECHANICAL_PROVIDER_TOKEN = new CapabilityToken<>() {};
    private static final CapabilityToken<IMechanicalEnergyConsumer> MECHANICAL_CONSUMER_TOKEN = new CapabilityToken<>() {};
    private static final CapabilityToken<IRadiationProvider> RADIATION_PROVIDER_TOKEN = new CapabilityToken<>() {};
    private static final CapabilityToken<IRadiationConsumer> RADIATION_CONSUMER_TOKEN = new CapabilityToken<>() {};

    @SubscribeEvent public static void onServerStarting(ServerStartingEvent event) {
        ICLib.IC_LOGGER.info("HELLO FROM SERVER STARTING");
    }
}
