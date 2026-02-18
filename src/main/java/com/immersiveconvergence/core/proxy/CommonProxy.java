package com.immersiveconvergence.core.proxy;

import com.immersiveconvergence.core.lib.ICLib;
import net.minecraftforge.eventbus.api.IEventBus;

@SuppressWarnings("unused")
public class CommonProxy {
    public static void modConstruction(IEventBus event) {
        ICLib.IC_LOGGER.info("Registering IC API!");
    }
}
