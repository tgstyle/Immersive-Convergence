package com.immersiveconvergence.core.registration;

import com.immersiveconvergence.core.lib.ICLib;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ICItems {
    public static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(ICLib.MODID);

    public static void init(IEventBus bus) { REGISTER.register(bus); }
}
