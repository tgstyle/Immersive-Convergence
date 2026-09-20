package com.immersiveconvergence.core;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import zone.rong.mixinbooter.Context;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;

@IFMLLoadingPlugin.Name("ICMixin")
@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class ICMixin implements IFMLLoadingPlugin, IEarlyMixinLoader {
    public static final Logger LOGGER = LogManager.getLogger("Immersive Convergence Mixin");

    private static final String IE_EARLY_CONFIG = "mixins.immersiveconvergence.ie.early.json";
    private static final String IP_EARLY_CONFIG = "mixins.immersiveconvergence.ip.early.json";
    private static final String IE_PROBE_CLASS = "blusunrize/immersiveengineering/common/blocks/BlockIEBase.class";
    private static final String IP_PROBE_CLASS = "flaxbeard/immersivepetroleum/common/blocks/BlockIPBase.class";
    private static final String[] PROBE_CLASSES = {IE_PROBE_CLASS, IP_PROBE_CLASS};
    private static Set<String> installedProbes;

    @Override public String[] getASMTransformerClass() { return new String[0]; }

    @Override public String getModContainerClass() { return null; }

    @Override public String getSetupClass() { return null; }

    @Override public void injectData(Map<String, Object> data) {}

    @Override public String getAccessTransformerClass() { return null; }

    @Override public List<String> getMixinConfigs() { return Arrays.asList("mixins.immersiveconvergence.early.json", IE_EARLY_CONFIG, IP_EARLY_CONFIG); }

    @Override public boolean shouldMixinConfigQueue(Context context) {
        String config = context.mixinConfig();
        if (IE_EARLY_CONFIG.equals(config)) { return installed(context, "immersiveengineering", IE_PROBE_CLASS); }
        if (IP_EARLY_CONFIG.equals(config)) { return installed(context, "immersivepetroleum", IP_PROBE_CLASS); }
        return true;
    }

    private static boolean installed(Context context, String modId, String probeClass) {
        if (context.isModPresent(modId)) { return true; }
        if (installedProbes == null) { installedProbes = scanModDirectories(); }
        boolean found = installedProbes.contains(probeClass);
        if (found) { LOGGER.info("{} is installed but was not listed at the coremod phase; queueing its early mixins anyway.", modId); }
        return found;
    }

    private static Set<String> scanModDirectories() {
        Set<String> found = new HashSet<>();
        File mods = new File(Launch.minecraftHome != null ? Launch.minecraftHome : new File("."), "mods");
        File[] nested = mods.listFiles(File::isDirectory);
        collectProbes(mods, found);
        if (nested != null) { for (File dir : nested) { collectProbes(dir, found); } }
        return found;
    }

    private static void collectProbes(File directory, Set<String> found) {
        File[] files = directory.listFiles();
        if (files == null) { return; }
        for (File file : files) {
            if (!file.isFile() || !file.getName().toLowerCase(Locale.ROOT).endsWith(".jar")) { continue; }
            try (JarFile jar = new JarFile(file)) {
                for (String probe : PROBE_CLASSES) {
                    if (jar.getEntry(probe) != null) { found.add(probe); }
                }
            }
            catch (IOException e) { LOGGER.debug("Could not read {} while looking for installed mods.", file.getName(), e); }
        }
    }
}
