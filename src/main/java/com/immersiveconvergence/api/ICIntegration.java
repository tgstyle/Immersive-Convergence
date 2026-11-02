package com.immersiveconvergence.api;

import com.immersiveconvergence.api.wires.IICWireCoil;
import com.immersiveconvergence.api.bridge.ICBridges;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused") public class ICIntegration {
    public static final String MODID = "immersiveengineering";
    private static final List<Runnable> RENDER_CACHE_CLEARERS = new ArrayList<>();

    public static void addRenderCacheClearer(Runnable clearer) {
        RENDER_CACHE_CLEARERS.add(clearer);
        if (ICMods.immersiveEngineering()) { ICBridges.integration().addRenderCacheClearer(clearer); }
    }

    public static void clearRenderCaches() {
        if (ICMods.immersiveEngineering()) {
            ICBridges.integration().clearRenderCaches();
            return;
        }
        for (Runnable clearer : RENDER_CACHE_CLEARERS) { clearer.run(); }
    }

    public static void putManualInt(String key, int value) {
        if (ICMods.immersiveEngineering()) { ICBridges.integration().putManualInt(key, value); }
    }

    public static void refreshConfig(FMLPreInitializationEvent event) {
        if (ICMods.immersiveEngineering()) { ICBridges.integration().refreshConfig(event); }
    }

    @Nullable public static Fluid creosote() { return ICMods.immersiveEngineering() ? ICBridges.integration().creosote() : FluidRegistry.getFluid("creosote"); }

    public static ItemStack cokeOven() { return ICMods.immersiveEngineering() ? ICBridges.integration().cokeOven() : ItemStack.EMPTY; }

    public static boolean isWireCoil(ItemStack stack) { return stack.getItem() instanceof IICWireCoil || (ICMods.immersiveEngineering() && ICBridges.integration().isWireCoil(stack)); }
}
