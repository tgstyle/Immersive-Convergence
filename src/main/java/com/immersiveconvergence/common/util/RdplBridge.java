package com.immersiveconvergence.common.util;

import mctmods.resourcedatapackloader.pack.PackManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.function.BiConsumer;

import javax.annotation.Nullable;

public final class RdplBridge {
    private RdplBridge() {}

    @Nullable public static Path root() { return PackManager.get().getRoot(); }

    @Nullable public static InputStream open(String namespace, String path) throws IOException { return PackManager.get().openRaw(namespace, path); }

    public static void forEach(String namespace, String folder, String ext, BiConsumer<String, String> consumer) {
        PackManager.get().forEach(folder, ext, (found, id, contents) -> { if (namespace.equals(found)) { consumer.accept(id, contents); } });
    }

    public static void registerDataFolders(String... folders) { PackManager.registerDataFolders(folders); }

    public static void rescan() {
        Path root = root();
        if (root != null) { PackManager.get().scan(root); }
    }
}
