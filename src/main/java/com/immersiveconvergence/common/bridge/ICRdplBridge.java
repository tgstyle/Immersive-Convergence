package com.immersiveconvergence.common.bridge;

import com.immersiveconvergence.api.bridge.IICRdplBridge;
import com.immersiveconvergence.common.util.RdplBridge;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.function.BiConsumer;

public final class ICRdplBridge implements IICRdplBridge {
    @Override @Nullable public Path root() { return RdplBridge.root(); }

    @Override @Nullable public InputStream open(@Nonnull String namespace, @Nonnull String path) throws IOException { return RdplBridge.open(namespace, path); }

    @Override public void forEach(@Nonnull String namespace, @Nonnull String folder, @Nonnull String ext, @Nonnull BiConsumer<String, String> consumer) { RdplBridge.forEach(namespace, folder, ext, consumer); }

    @Override public void rescan() { RdplBridge.rescan(); }
}
