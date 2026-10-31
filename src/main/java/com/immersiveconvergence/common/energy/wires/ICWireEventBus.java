package com.immersiveconvergence.common.energy.wires;

import com.immersiveconvergence.api.wires.ICWireEvent;

import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;

public final class ICWireEventBus {
    private ICWireEventBus() {}

    public static void post(@Nonnull ICWireEvent event) { MinecraftForge.EVENT_BUS.post(event); }

    public static boolean canceled(@Nonnull ICWireEvent.Placed event) { return MinecraftForge.EVENT_BUS.post(event); }

    public static boolean canceled(@Nonnull ICWireEvent.Shock event) { return MinecraftForge.EVENT_BUS.post(event); }
}
