package com.immersiveconvergence.core.integration.crafttweaker;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.core.integration.ICCompatModule;

import crafttweaker.CraftTweakerAPI;

public class ICCraftTweaker extends ICCompatModule {
    @Override public void preInit() {
        if (ICMods.immersivePetroleum()) { CraftTweakerAPI.registerClass(Reservoir.class); }
    }
}
