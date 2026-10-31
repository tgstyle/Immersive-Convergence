package com.immersiveconvergence.api.util;

import com.immersiveconvergence.api.bridge.ICBridges;

import net.minecraftforge.fml.common.Loader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ICResources {
    private ICResources() {}

    public static final String RDPL_MOD_ID = "resourcedatapackloader";
    private static final String RDPL_ASSETS = "assets";

    public static boolean deferToRdpl() { return Loader.isModLoaded(RDPL_MOD_ID); }

    public static Path overridesDir() { return Loader.instance().getConfigDir().toPath().getParent().resolve("overrides"); }

    public static Path overrideRoot(String modid) {
        Path rdpl = deferToRdpl() ? ICBridges.rdpl().root() : null;
        return rdpl != null ? rdpl.resolve(RDPL_ASSETS).resolve(modid) : overridesDir().resolve(modid);
    }

    public static void migrateToRdpl() {
        if (!deferToRdpl()) { return; }
        Path from = overridesDir();
        Path rdpl = ICBridges.rdpl().root();
        if (rdpl == null || !Files.isDirectory(from)) { return; }
        Path to = rdpl.resolve(RDPL_ASSETS);
        int moved = 0, kept = 0;
        try (Stream<Path> files = Files.walk(from)) {
            for (Path file : (Iterable<Path>) files.filter(Files::isRegularFile)::iterator) {
                Path relative = from.relativize(file);
                Path target = to.resolve(relative);
                if (Files.exists(target)) {
                    ICLogger.warn("Leaving override " + relative + " where it is: " + target + " already exists");
                    kept++;
                    continue;
                }
                Files.createDirectories(target.getParent());
                Files.move(file, target);
                ICLogger.info("Moved override " + relative + " into Resource Data Pack Loader's folder at " + target);
                moved++;
            }
            if (kept == 0) { deleteEmptyTree(from); }
        }
        catch (IOException e) { ICLogger.error("Could not move the overrides folder into Resource Data Pack Loader's folder - " + e.getMessage()); }
        if (moved > 0 || kept > 0) { ICLogger.info(moved + " override file(s) moved into " + to + (kept > 0 ? ", " + kept + " left in " + from : ", " + from + " removed") + "; Resource Data Pack Loader serves them from now on"); }
        if (moved > 0) { ICBridges.rdpl().rescan(); }
    }

    private static void deleteEmptyTree(Path root) throws IOException {
        List<Path> directories;
        try (Stream<Path> walk = Files.walk(root)) { directories = walk.filter(Files::isDirectory).sorted(Comparator.reverseOrder()).collect(Collectors.toList()); }
        for (Path directory : directories) {
            try (Stream<Path> children = Files.list(directory)) {
                if (children.findAny().isPresent()) { return; }
            }
            Files.delete(directory);
        }
    }

    public static InputStream openOverride(String modid, String path) throws IOException {
        if (deferToRdpl()) {
            InputStream override = ICBridges.rdpl().open(modid, path);
            if (override != null) {
                ICLogger.info("Using Resource Data Pack Loader override for " + modid + "/" + path);
                return override;
            }
        }
        else {
            Path override = overrideRoot(modid).resolve(path);
            if (Files.isRegularFile(override)) {
                ICLogger.info("Using override for " + modid + "/" + path);
                return Files.newInputStream(override);
            }
        }
        return null;
    }

    public static InputStream open(String modid, String path, String defaultPath) throws IOException {
        InputStream override = openOverride(modid, path);
        if (override != null) { return override; }
        try { return open(modid, defaultPath); }
        catch (FileNotFoundException e) { return open(modid, path); }
    }

    public static InputStream open(String modid, String path) throws IOException {
        InputStream override = openOverride(modid, path);
        if (override != null) { return override; }
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(String.format("assets/%s/%s", modid, path));
        if (stream == null) { throw new FileNotFoundException("assets/" + modid + "/" + path); }
        return stream;
    }
}
