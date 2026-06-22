package com.immersiveconvergence.core.proxy;

import com.immersiveconvergence.core.lib.ICLib;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;

@SuppressWarnings("unused")
public class CommonProxy {
    public static void modConstruction(IEventBus event) {
        ICLib.IC_LOGGER.info("Registering IC API!");
    }

    public void reinitializeGUI() {}

    public Level getClientWorld() { return null; }

    public Player getClientPlayer() { return null; }
}