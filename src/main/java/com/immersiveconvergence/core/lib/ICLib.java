package com.immersiveconvergence.core.lib;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

@SuppressWarnings("unused")
public class ICLib {
    public static final String MODID = "immersiveconvergence";
    public static final String VERSION = "1.0.0";
    public static final Logger IC_LOGGER = LogUtils.getLogger();

    public static ResourceLocation makeTextureLocation(String name) { return rl("textures/gui/" + name + ".png"); }

    public static ResourceLocation rl(String name) { return ResourceLocation.fromNamespaceAndPath(ICLib.MODID, name); }

    public static float remapRange(float inMin, float inMax, float outMin, float outMax, float value) { return outMin + ((value - inMin) / inMax) * (outMax - outMin); }
}
