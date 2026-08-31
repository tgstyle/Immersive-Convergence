package com.immersiveconvergence.common.util;

import net.minecraftforge.fml.common.Loader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ICResources {
    private ICResources() {}

    public static Path overrideRoot(String modid) { return Loader.instance().getConfigDir().toPath().getParent().resolve("overrides").resolve(modid); }

    public static InputStream open(String modid, String path) throws IOException {
        Path override = overrideRoot(modid).resolve(path);
        if (Files.isRegularFile(override)) {
            ICLogger.info("Using override for " + modid + "/" + path);
            return Files.newInputStream(override);
        }
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(String.format("assets/%s/%s", modid, path));
        if (stream == null) { throw new FileNotFoundException("assets/" + modid + "/" + path); }
        return stream;
    }
}
