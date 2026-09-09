package com.immersiveconvergence.common;

import blusunrize.immersiveengineering.api.IEApi;
import blusunrize.immersiveengineering.api.energy.wires.IWireCoil;
import blusunrize.immersiveengineering.common.Config;
import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.blocks.stone.BlockTypes_StoneDevices;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public final class IEIntegrationBridge {
    private IEIntegrationBridge() {}

    public static void addRenderCacheClearer(Runnable clearer) { IEApi.renderCacheClearers.add(clearer); }

    public static void clearRenderCaches() {
        for (Runnable clearer : IEApi.renderCacheClearers) { clearer.run(); }
    }

    public static void putManualInt(String key, int value) { Config.manual_int.put(key, value); }

    public static void refreshConfig(FMLPreInitializationEvent event) { Config.preInit(event); }

    public static Fluid creosote() { return IEContent.fluidCreosote; }

    public static ItemStack cokeOven() { return new ItemStack(IEContent.blockStoneDevice, 1, BlockTypes_StoneDevices.COKE_OVEN.getMeta()); }

    public static boolean isWireCoil(ItemStack stack) { return stack.getItem() instanceof IWireCoil; }
}
