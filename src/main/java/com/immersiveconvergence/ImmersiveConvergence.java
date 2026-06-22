package com.immersiveconvergence;

import com.immersiveconvergence.core.ICCommonConfig;
import com.immersiveconvergence.core.lib.ICLib;
import com.immersiveconvergence.core.proxy.CommonProxy;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@SuppressWarnings("unused")
@Mod(ICLib.MODID)
public class ImmersiveConvergence {

    private static CommonProxy proxy;

    public static CommonProxy getProxy() {
        if (proxy == null) {
            proxy = FMLEnvironment.dist.isClient()
                    ? loadClientProxy()
                    : new CommonProxy();
        }
        return proxy;
    }

    private static CommonProxy loadClientProxy() {
        try {
            return (CommonProxy) Class.forName(
                    "com.immersiveconvergence.core.proxy.ClientProxySupplier"
            ).getMethod("get").invoke(null);
        } catch (Exception e) {
            ICLib.IC_LOGGER.error("Failed to load client proxy", e);
            return new CommonProxy();
        }
    }

    public ImmersiveConvergence(IEventBus modEventBus, ModContainer container) {
        modEventBus.addListener(this::commonSetup);
        CommonProxy.modConstruction(modEventBus);
        container.registerConfig(ModConfig.Type.COMMON, ICCommonConfig.SPEC);
        NeoForge.EVENT_BUS.register(ImmersiveConvergence.class);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {}

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {}
}