package com.immersiveconvergence.common.util.compat.waila;

import com.immersiveconvergence.common.util.compat.ICCompatModule;

import net.minecraftforge.fml.common.event.FMLInterModComms;

public class ICWailaHelper extends ICCompatModule {
    @Override public void preInit() {}

    @Override public void init() {
        FMLInterModComms.sendMessage("waila", "register", ICWailaDataProvider.class.getName() + ".callbackRegister");
    }
}
