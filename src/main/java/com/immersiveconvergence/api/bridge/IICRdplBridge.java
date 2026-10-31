package com.immersiveconvergence.api.bridge;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.function.BiConsumer;

public interface IICRdplBridge {
    @Nullable Path root();

    @Nullable InputStream open(@Nonnull String namespace, @Nonnull String path) throws IOException;

    void forEach(@Nonnull String namespace, @Nonnull String folder, @Nonnull String ext, @Nonnull BiConsumer<String, String> consumer);

    void rescan();
}
