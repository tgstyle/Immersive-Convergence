package com.immersiveconvergence.core.lib;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class ICLib {
    public static final String MODID = "immersiveconvergence";
    public static final Logger IC_LOGGER = LogUtils.getLogger();
    public static final String DESC_INFO = "desc." + MODID + ".info.";
    public static final int COLOR_ORANGE = 0xfff78034;
    public static final int COLOR_ORANGE_SHADOW = 0xff3e200d;

    public static ResourceLocation makeTextureLocation(String name) { return rl("textures/gui/" + name + ".png"); }

    public static ResourceLocation rl(String name) { return ResourceLocation.fromNamespaceAndPath(ICLib.MODID, name); }
}
