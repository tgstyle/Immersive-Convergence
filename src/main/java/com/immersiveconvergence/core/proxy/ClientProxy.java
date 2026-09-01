package com.immersiveconvergence.core.proxy;

import com.immersiveconvergence.client.gui.RotorCreativeScreen;
import com.immersiveconvergence.client.models.ICDynamicModel;
import com.immersiveconvergence.client.models.ICRotorModels;
import com.immersiveconvergence.client.renderer.RotorCreativeRenderer;
import com.immersiveconvergence.common.blocks.gui.RotorCreativeMenu;
import com.immersiveconvergence.core.lib.ICLib;
import com.immersiveconvergence.core.registration.ICBlockEntities;
import com.immersiveconvergence.core.registration.ICMenuTypes;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ICLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy extends CommonProxy {
    @SubscribeEvent public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(ICMenuTypes.ROTOR_CREATIVE.getType(), (RotorCreativeMenu menu, Inventory inv, Component title) -> new RotorCreativeScreen(menu, inv)));
    }

    @SubscribeEvent public static void registerModelLoaders(ModelEvent.RegisterGeometryLoaders event) {
        ICRotorModels.ROTOR_CREATIVE = new ICDynamicModel("rotor_creative");
        ICRotorModels.ROTOR_CREATIVE_EAST_WEST = new ICDynamicModel("rotor_creative_east_west");
    }

    @SubscribeEvent public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) { event.registerBlockEntityRenderer(ICBlockEntities.ROTOR_CREATIVE.get(), context -> new RotorCreativeRenderer()); }
}
