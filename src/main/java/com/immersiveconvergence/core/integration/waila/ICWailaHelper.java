package com.immersiveconvergence.core.integration.waila;

import com.immersiveconvergence.core.integration.ICCompatModule;

import net.minecraftforge.fml.common.event.FMLInterModComms;

public class ICWailaHelper extends ICCompatModule {
    @Override public void preInit() {}

    @Override public void init() { FMLInterModComms.sendMessage("waila", "register", ICWailaDataProvider.class.getName() + ".callbackRegister"); }
}
