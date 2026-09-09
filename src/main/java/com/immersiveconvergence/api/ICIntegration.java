package com.immersiveconvergence.api;

import com.immersiveconvergence.common.IEIntegrationBridge;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ICIntegration {
    public static final String MODID = "immersiveengineering";
    private static final List<Runnable> RENDER_CACHE_CLEARERS = new ArrayList<>();

    public static void addRenderCacheClearer(Runnable clearer) {
        RENDER_CACHE_CLEARERS.add(clearer);
        if (ICMods.immersiveEngineering()) { IEIntegrationBridge.addRenderCacheClearer(clearer); }
    }

    public static void clearRenderCaches() {
        if (ICMods.immersiveEngineering()) {
            IEIntegrationBridge.clearRenderCaches();
            return;
        }
        for (Runnable clearer : RENDER_CACHE_CLEARERS) { clearer.run(); }
    }

    public static void putManualInt(String key, int value) {
        if (ICMods.immersiveEngineering()) { IEIntegrationBridge.putManualInt(key, value); }
    }

    public static void refreshConfig(FMLPreInitializationEvent event) {
        if (ICMods.immersiveEngineering()) { IEIntegrationBridge.refreshConfig(event); }
    }

    @Nullable public static Fluid creosote() { return ICMods.immersiveEngineering() ? IEIntegrationBridge.creosote() : null; }

    public static ItemStack cokeOven() { return ICMods.immersiveEngineering() ? IEIntegrationBridge.cokeOven() : ItemStack.EMPTY; }

    public static boolean isWireCoil(ItemStack stack) { return ICMods.immersiveEngineering() && IEIntegrationBridge.isWireCoil(stack); }
}
