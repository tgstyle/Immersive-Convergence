package com.immersiveconvergence.core.util;

import com.immersiveconvergence.core.lib.ICLib;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ICServerEvents {
    @SubscribeEvent public static void onServerStarting(ServerStartingEvent event) { ICLib.IC_LOGGER.info("HELLO FROM SERVER STARTING"); }
}
