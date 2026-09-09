package com.immersiveconvergence.common.util.compat.crafttweaker;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.common.util.compat.ICCompatModule;

import crafttweaker.CraftTweakerAPI;

public class ICCraftTweaker extends ICCompatModule {
    @Override public void preInit() {
        if (ICMods.immersivePetroleum()) { CraftTweakerAPI.registerClass(Reservoir.class); }
    }
}
