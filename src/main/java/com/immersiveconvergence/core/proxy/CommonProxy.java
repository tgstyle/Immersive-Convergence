package com.immersiveconvergence.core.proxy;

import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.world.entity.player.Player;

public class CommonProxy {
    public static void modConstruction() { ICLib.IC_LOGGER.info("Registering IC API!"); }

    public Player getClientPlayer() { return null; }
}
