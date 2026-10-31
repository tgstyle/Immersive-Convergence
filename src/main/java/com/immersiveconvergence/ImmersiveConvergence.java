package com.immersiveconvergence;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.bridge.ICBridges;
import com.immersiveconvergence.api.multiblock.ICTickingRegistry;
import com.immersiveconvergence.api.multiblock.QueueProcessor;
import com.immersiveconvergence.api.util.ICLogger;
import com.immersiveconvergence.api.util.ICResources;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.api.wires.ICWires;
import com.immersiveconvergence.common.energy.IEWireBridge;
import com.immersiveconvergence.common.energy.wires.ICWireService;
import com.immersiveconvergence.common.util.IEToolboxBridge;
import com.immersiveconvergence.common.multiblock.IEMultiblockRegistry;
import com.immersiveconvergence.common.multiblock.IEMultiblocks;
import com.immersiveconvergence.common.CommonProxy;
import com.immersiveconvergence.common.ICContent;
import com.immersiveconvergence.common.bridge.ICBridgeService;
import com.immersiveconvergence.common.event.ICTickingService;
import com.immersiveconvergence.common.registry.ICRegistryRemaps;
import com.immersiveconvergence.common.blocks.conveyors.ICConveyorRegistry;
import com.immersiveconvergence.common.blocks.pipes.ICPipeRegistry;
import com.immersiveconvergence.common.util.compat.ICCompatModule;
import com.immersiveconvergence.common.util.RdplBridge;
import com.immersiveconvergence.core.ICCommonConfig;
import com.immersiveconvergence.core.network.ICWireDeltaPacket;
import com.immersiveconvergence.core.network.ICWireSyncPacket;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
@Mod(modid = ImmersiveConvergence.MODID, name = ImmersiveConvergence.NAME, acceptedMinecraftVersions = "[1.12.2,1.13)", dependencies = "after:immersiveengineering@[0.12-92,);" + "required-after:mixinbooter@[10.7,);" + "required-after:forge@[14.23.3.2655,);")
public class ImmersiveConvergence {

    public static final String MODID = "immersiveconvergence";
    public static final String NAME = "Immersive Convergence";

    @SidedProxy(clientSide = "com.immersiveconvergence.client.ClientProxy", serverSide = "com.immersiveconvergence.common.CommonProxy")
    public static CommonProxy proxy;
    public static final SimpleNetworkWrapper packetHandler = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

    @Instance(MODID) public static ImmersiveConvergence instance;

    public static final CreativeTabs creativeTab = new CreativeTabs(MODID) {
        @Override @Nonnull public ItemStack createIcon() { return new ItemStack(ICContent.blockDevice, 1, 0); }
    };

    @EventHandler public void preInit(FMLPreInitializationEvent event) {
        ICLogger.logger = event.getModLog();
        ICBridges.install(new ICBridgeService());
        ConfigManager.sync(MODID, Type.INSTANCE);
        ICMods.init(ICCommonConfig.experimental.disableImmersiveEngineering);
        if (ICCommonConfig.experimental.disableImmersiveEngineering) {
            ICLogger.logger.warn("Experimental: disableImmersiveEngineering is on, so Immersive Engineering and Immersive Petroleum are being treated as absent. Their recipes, multiblocks and placed blocks will not work. You said you can replace the needed recipes and NBT data yourself.");
        }
        if (ICResources.deferToRdpl()) { RdplBridge.registerDataFolders("recipes_multiblocks", "multiblocks"); }
        ICResources.migrateToRdpl();
        ICWires.install(new ICWireService());
        QueueProcessor.queueEnabled = () -> ICCommonConfig.multiblocks.disassemblyMode == ICCommonConfig.DisassemblyMode.PROCESS_QUEUE;
        MinecraftForge.EVENT_BUS.register(ICRegistryRemaps.class);
        ICTickingService ticking = new ICTickingService();
        ICTickingRegistry.install(ticking);
        MinecraftForge.EVENT_BUS.register(ticking);
        ICContent.preInit();
        ICCompatModule.preInitAll();
        if (ICMods.immersiveEngineering()) { IEMultiblocks.init(); }
        ICPipeRegistry.register();
        proxy.preInit();
        ICConveyorRegistry.register();
    }

    @EventHandler public void init(FMLInitializationEvent event) {
        if (ICMods.immersiveEngineering()) {
            IEWireBridge.mirrorWireTypes();
            IEMultiblocks.alignRenderLayers();
            IEToolboxBridge.registerToolType();
        }
        ICCompatModule.initAll();
        packetHandler.registerMessage(ICWireSyncPacket.Handler.class, ICWireSyncPacket.class, 4, Side.CLIENT);
        packetHandler.registerMessage(ICWireDeltaPacket.Handler.class, ICWireDeltaPacket.class, 5, Side.CLIENT);
        proxy.init();
    }

    @EventHandler public void postInit(FMLPostInitializationEvent event) {
        if (ICMods.immersiveEngineering()) {
            IEMultiblockRegistry.loadAll();
            IEWireBridge.adoptWireTypes();
        }
        ICWireType.freeze();
        proxy.postInit();
    }

    @EventHandler public void loadComplete(FMLLoadCompleteEvent event) { proxy.loadComplete(); }
}
