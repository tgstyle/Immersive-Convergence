package com.immersiveconvergence;

import com.immersiveconvergence.api.HeatCapabilities;
import com.immersiveconvergence.api.MechanicalCapabilities;
import com.immersiveconvergence.api.capability.IHeatConsumer;
import com.immersiveconvergence.api.capability.IHeatProvider;
import com.immersiveconvergence.api.capability.IMechanicalEnergyConsumer;
import com.immersiveconvergence.api.capability.IMechanicalEnergyProvider;
import com.immersiveconvergence.core.ICCommonConfig;
import com.immersiveconvergence.core.lib.ICLib;
import com.immersiveconvergence.core.proxy.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@SuppressWarnings("unused")
@Mod(ICLib.MODID)
public class ImmersiveConvergence {
    public ImmersiveConvergence(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerCapabilities);

        CommonProxy.modConstruction(modEventBus);

        context.registerConfig(ModConfig.Type.COMMON, ICCommonConfig.SPEC);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IHeatProvider.class);
        event.register(IHeatConsumer.class);
        event.register(IMechanicalEnergyProvider.class);
        event.register(IMechanicalEnergyConsumer.class);
        HeatCapabilities.HEAT_PROVIDER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
        HeatCapabilities.HEAT_CONSUMER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
        MechanicalCapabilities.MECHANICAL_PROVIDER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
        MechanicalCapabilities.MECHANICAL_CONSUMER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ICLib.IC_LOGGER.info("HELLO FROM COMMON SETUP");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        ICLib.IC_LOGGER.info("HELLO FROM SERVER STARTING");
    }
}
