package com.immersiveconvergence;

import com.immersiveconvergence.common.CommonProxy;
import com.immersiveconvergence.common.util.ICLogger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ImmersiveConvergence.MODID, name = ImmersiveConvergence.NAME, acceptedMinecraftVersions = "[1.12.2,1.13)", dependencies = "required-after:immersiveengineering@[0.12-92,);" + "required-after:forge@[14.23.5.2860,);")
public class ImmersiveConvergence {

    public static final String MODID = "immersiveconvergence";
    public static final String NAME = "Immersive Convergence";

    @SidedProxy(clientSide = "com.immersiveconvergence.client.ClientProxy", serverSide = "com.immersiveconvergence.common.CommonProxy")
    public static CommonProxy proxy;

    @Instance(MODID) public static ImmersiveConvergence instance;

    @EventHandler public void preInit(FMLPreInitializationEvent event) {
        ICLogger.logger = event.getModLog();
        proxy.preInit();
    }

    @EventHandler public void init(FMLInitializationEvent event) { proxy.init(); }

    @EventHandler public void postInit(FMLPostInitializationEvent event) { proxy.postInit(); }
}
