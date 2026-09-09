package com.immersiveconvergence.core;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import zone.rong.mixinbooter.Context;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@IFMLLoadingPlugin.Name("ICMixin")
@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class ICMixin implements IFMLLoadingPlugin, IEarlyMixinLoader {
    public static final Logger LOGGER = LogManager.getLogger("Immersive Convergence Mixin");

    private static final String IE_EARLY_CONFIG = "mixins.immersiveconvergence.ie.early.json";
    private static final String IP_EARLY_CONFIG = "mixins.immersiveconvergence.ip.early.json";

    @Override public String[] getASMTransformerClass() { return new String[0]; }

    @Override public String getModContainerClass() { return null; }

    @Override public String getSetupClass() { return null; }

    @Override public void injectData(Map<String, Object> data) {}

    @Override public String getAccessTransformerClass() { return null; }

    @Override public List<String> getMixinConfigs() { return Arrays.asList("mixins.immersiveconvergence.early.json", IE_EARLY_CONFIG, IP_EARLY_CONFIG); }

    @Override public boolean shouldMixinConfigQueue(Context context) {
        String config = context.mixinConfig();
        if (IE_EARLY_CONFIG.equals(config)) { return context.isModPresent("immersiveengineering"); }
        if (IP_EARLY_CONFIG.equals(config)) { return context.isModPresent("immersivepetroleum"); }
        return true;
    }
}
