package com.immersiveconvergence.core.proxy;

import com.immersiveconvergence.core.lib.ICLib;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = ICLib.MODID, value = Dist.CLIENT)
public class ClientProxy extends CommonProxy {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {}
}
