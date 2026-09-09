package com.immersiveconvergence.common.blocks.conveyors;

import com.immersiveconvergence.api.ICConveyors;
import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.client.ICModels;
import com.immersiveconvergence.common.util.ICLogger;
import com.immersiveconvergence.core.ICMixinConfig;

import net.minecraftforge.fml.common.FMLCommonHandler;

public final class ICConveyorRegistry {
    private ICConveyorRegistry() {}

    public static void register() {
        if (!ICMixinConfig.mixinSettings.replaceIEConveyors || !ICMods.immersiveEngineering()) { return; }
        try {
            ICConveyors.registerBelt("conveyor", ConveyorBasicAlternative.class);
            ICConveyors.registerBelt("uncontrolled", ConveyorUncontrolledAlternative.class);
            ICConveyors.registerBelt("splitter", ConveyorSplitAlternative.class);
            ICConveyors.registerBelt("covered", ConveyorCoveredAlternative.class);
            ICConveyors.registerBelt("dropper", ConveyorDropAlternative.class);
            ICConveyors.registerBelt("droppercovered", ConveyorDropCoveredAlternative.class);
            ICConveyors.registerBelt("extract", ConveyorExtractAlternative.class);
            ICConveyors.registerBelt("extractcovered", ConveyorExtractCoveredAlternative.class);
            ICConveyors.registerBelt("vertical", ConveyorVerticalAlternative.class);
            ICConveyors.registerBelt("verticalcovered", ConveyorVerticalCoveredAlternative.class);
            if (FMLCommonHandler.instance().getSide().isClient()) {
                try { ICModels.clearConveyorModelCaches(); }
                catch (Exception e) { ICLogger.error("Failed to clear the conveyor model caches: " + e); }
            }
            ICLogger.info("Conveyor replacements registered");
        }
        catch (Exception e) { ICLogger.error("Failed to register the conveyor replacements: " + e); }
    }
}
