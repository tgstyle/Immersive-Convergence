package com.immersiveconvergence.core.proxy;

import com.immersiveconvergence.client.gui.RotorCreativeScreen;
import com.immersiveconvergence.client.models.ICDynamicModel;
import com.immersiveconvergence.client.models.ICRotorModels;
import com.immersiveconvergence.client.renderer.RotorCreativeRenderer;
import com.immersiveconvergence.core.lib.ICLib;
import com.immersiveconvergence.core.registration.ICBlockEntities;
import com.immersiveconvergence.core.registration.ICMenuTypes;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = ICLib.MODID, value = Dist.CLIENT)
public class ClientProxy extends CommonProxy {

    @SubscribeEvent public static void onClientSetup(FMLClientSetupEvent event) {
    }

    @SubscribeEvent public static void registerModelLoaders(ModelEvent.RegisterGeometryLoaders event) {
        ICRotorModels.ROTOR_CREATIVE = new ICDynamicModel("rotor_creative");
        ICRotorModels.ROTOR_CREATIVE_EAST_WEST = new ICDynamicModel("rotor_creative_east_west");
    }

    @SubscribeEvent public static void registerMenuScreens(RegisterMenuScreensEvent event) { event.register(ICMenuTypes.ROTOR_CREATIVE.getType(), RotorCreativeScreen::new); }

    @SubscribeEvent public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) { event.registerBlockEntityRenderer(ICBlockEntities.ROTOR_CREATIVE.get(), ctx -> new RotorCreativeRenderer()); }
}
