package com.immersiveconvergence.common.energy.wires;

import com.immersiveconvergence.api.wires.ICWireEvent;

import net.neoforged.neoforge.common.NeoForge;

import javax.annotation.Nonnull;

public final class ICWireEventBus {
    private ICWireEventBus() {}

    public static void post(@Nonnull ICWireEvent event) { NeoForge.EVENT_BUS.post(event); }

    public static boolean canceled(@Nonnull ICWireEvent.Placed event) { return NeoForge.EVENT_BUS.post(event).isCanceled(); }

    public static boolean canceled(@Nonnull ICWireEvent.Shock event) { return NeoForge.EVENT_BUS.post(event).isCanceled(); }
}
