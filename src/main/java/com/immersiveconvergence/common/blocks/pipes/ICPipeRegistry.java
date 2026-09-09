package com.immersiveconvergence.common.blocks.pipes;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.common.util.ICLogger;
import com.immersiveconvergence.core.ICMixinConfig;

import blusunrize.immersiveengineering.common.blocks.metal.TileEntityFluidPipe;

public final class ICPipeRegistry {
    private ICPipeRegistry() {}

    public static boolean replacing() { return ICMixinConfig.mixinSettings.replaceIEPipes && ICMods.immersiveEngineering(); }

    public static void register() {
        if (!replacing()) { return; }
        TileEntityFluidPipe.initCovers();
        ICLogger.info("Fluid pipe replacements registered");
    }
}
