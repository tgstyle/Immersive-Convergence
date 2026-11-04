package com.immersiveconvergence;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.common.ICContent;
import com.immersiveconvergence.common.blocks.conveyors.ICConveyorRegistry;
import com.immersiveconvergence.common.blocks.pipes.ICPipeRegistry;
import com.immersiveconvergence.common.energy.IEWireBridge;
import com.immersiveconvergence.common.util.IEToolboxBridge;
import com.immersiveconvergence.core.integration.ICCompatModule;
import com.immersiveconvergence.core.integration.immersiveengineering.IEMultiblockRegistry;
import com.immersiveconvergence.core.integration.immersiveengineering.IEMultiblocks;
import com.immersiveconvergence.core.network.ICWireDeltaPacket;
import com.immersiveconvergence.core.network.ICWireSyncPacket;
import com.immersiveconvergence.core.proxy.CommonProxy;
import com.immersiveconvergence.core.registration.ICCreativeTab;
import com.immersiveconvergence.core.util.ICStartup;
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

@SuppressWarnings("unused") @Mod(modid = ImmersiveConvergence.MODID, name = ImmersiveConvergence.NAME, acceptedMinecraftVersions = "[1.12.2,1.13)", dependencies = "after:immersiveengineering@[0.12-92,);" + "after:resourcedatapackloader@[1.0.130,);" + "required-after:mixinbooter@[10.7,);" + "required-after:forge@[14.23.3.2655,);") public class ImmersiveConvergence {

    public static final String MODID = "immersiveconvergence";
    public static final String NAME = "Immersive Convergence";
    @SidedProxy(clientSide = "com.immersiveconvergence.core.proxy.ClientProxy", serverSide = "com.immersiveconvergence.core.proxy.CommonProxy") public static CommonProxy proxy;
    public static final SimpleNetworkWrapper packetHandler = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
    @Instance(MODID) public static ImmersiveConvergence instance;

    static { ICCreativeTab.register(); }

    @EventHandler public void preInit(FMLPreInitializationEvent event) {
        ICStartup.configure(event);
        ICStartup.installServices();
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
