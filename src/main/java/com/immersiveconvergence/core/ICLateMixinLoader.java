package com.immersiveconvergence.core;

import zone.rong.mixinbooter.Context;
import zone.rong.mixinbooter.ILateMixinLoader;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class ICLateMixinLoader implements ILateMixinLoader {
    private static final String IE_CONFIG = "mixins.immersiveconvergence.json";
    private static final String IP_CONFIG = "mixins.immersiveconvergence.ip.json";
    private static final String IP_PUMPJACK_CONFIG = "mixins.immersiveconvergence.ip.pumpjack.json";
    private static final String FORGE_CONFIG = "mixins.immersiveconvergence.forge.json";

    @Override public List<String> getMixinConfigs() { return Arrays.asList(IE_CONFIG, IP_CONFIG, IP_PUMPJACK_CONFIG, FORGE_CONFIG); }

    @Override public boolean shouldMixinConfigQueue(Context context) {
        String config = context.mixinConfig();
        if (IE_CONFIG.equals(config)) { return context.isModPresent("immersiveengineering"); }
        if (IP_CONFIG.equals(config)) { return context.isModPresent("immersivepetroleum"); }
        if (IP_PUMPJACK_CONFIG.equals(config)) { return context.isModPresent("immersivepetroleum") && !context.isModPresent("tweakedpetroleum"); }
        return true;
    }
}
