package com.immersiveconvergence.api;

import blusunrize.immersiveengineering.api.IEApi;
import blusunrize.immersiveengineering.api.energy.wires.IWireCoil;
import blusunrize.immersiveengineering.common.Config;
import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.blocks.stone.BlockTypes_StoneDevices;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@SuppressWarnings("unused")
public class ICIntegration {
    public static final String MODID = blusunrize.immersiveengineering.ImmersiveEngineering.MODID;

    public static void addRenderCacheClearer(Runnable clearer) { IEApi.renderCacheClearers.add(clearer); }

    public static void clearRenderCaches() { for (Runnable r : IEApi.renderCacheClearers) { r.run(); } }

    public static void putManualInt(String key, int value) { Config.manual_int.put(key, value); }

    public static void refreshConfig(FMLPreInitializationEvent event) { Config.preInit(event); }

    public static Fluid creosote() { return IEContent.fluidCreosote; }

    public static ItemStack cokeOven() { return new ItemStack(IEContent.blockStoneDevice, 1, BlockTypes_StoneDevices.COKE_OVEN.getMeta()); }

    public static boolean isWireCoil(ItemStack stack) { return stack.getItem() instanceof IWireCoil; }
}
