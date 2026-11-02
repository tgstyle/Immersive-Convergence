package com.immersiveconvergence.core.util;

import com.immersiveconvergence.ImmersiveConvergence;
import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.bridge.ICBridges;
import com.immersiveconvergence.api.multiblock.ICTickingRegistry;
import com.immersiveconvergence.api.multiblock.QueueProcessor;
import com.immersiveconvergence.api.util.ICLogger;
import com.immersiveconvergence.api.util.ICResources;
import com.immersiveconvergence.api.wires.ICWires;
import com.immersiveconvergence.common.bridge.ICBridgeService;
import com.immersiveconvergence.common.energy.wires.ICWireService;
import com.immersiveconvergence.common.event.ICTickingService;
import com.immersiveconvergence.common.registry.ICRegistryRemaps;
import com.immersiveconvergence.common.util.RdplBridge;
import com.immersiveconvergence.core.config.ICCommonConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ICStartup {
    public static void configure(FMLPreInitializationEvent event) {
        ICLogger.logger = event.getModLog();
        ICBridges.install(new ICBridgeService());
        ConfigManager.sync(ImmersiveConvergence.MODID, Type.INSTANCE);
        ICMods.init(ICCommonConfig.experimental.disableImmersiveEngineering);
        if (ICCommonConfig.experimental.disableImmersiveEngineering) { ICLogger.logger.warn("Experimental: disableImmersiveEngineering is on, so Immersive Engineering and Immersive Petroleum are being treated as absent. Their recipes, multiblocks and placed blocks will not work. You said you can replace the needed recipes and NBT data yourself."); }
        if (ICResources.deferToRdpl()) { RdplBridge.registerDataFolders("recipes_multiblocks", "multiblocks"); }
        ICResources.migrateToRdpl();
    }

    public static void installServices() {
        ICWires.install(new ICWireService());
        QueueProcessor.queueEnabled = () -> ICCommonConfig.multiblocks.disassemblyMode == ICCommonConfig.DisassemblyMode.PROCESS_QUEUE;
        MinecraftForge.EVENT_BUS.register(ICRegistryRemaps.class);
        ICTickingService ticking = new ICTickingService();
        ICTickingRegistry.install(ticking);
        MinecraftForge.EVENT_BUS.register(ticking);
    }
}
