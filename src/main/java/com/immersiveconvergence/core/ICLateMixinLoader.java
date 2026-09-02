package com.immersiveconvergence.core;

import zone.rong.mixinbooter.Context;
import zone.rong.mixinbooter.ILateMixinLoader;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class ICLateMixinLoader implements ILateMixinLoader {
    private static final String IP_CONFIG = "mixins.immersiveconvergence.ip.json";

    @Override public List<String> getMixinConfigs() { return Arrays.asList("mixins.immersiveconvergence.json", IP_CONFIG); }

    @Override public boolean shouldMixinConfigQueue(Context context) { return !IP_CONFIG.equals(context.mixinConfig()) || context.isModPresent("immersivepetroleum"); }
}
