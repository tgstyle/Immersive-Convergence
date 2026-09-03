package com.immersiveconvergence.client;

import com.immersiveconvergence.common.util.ICLogger;
import com.immersiveconvergence.common.util.ICResources;

import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public final class OverridesResourcePack implements IResourcePack {
    private static OverridesResourcePack instance;
    private final Path root;

    private OverridesResourcePack(Path root) { this.root = root; }

    public static List<IResourcePack> insert(List<IResourcePack> packs) {
        if (ICResources.deferToRdpl()) { return packs; }
        if (instance == null) { instance = new OverridesResourcePack(ICResources.overridesDir()); }
        if (packs.contains(instance)) { return packs; }
        List<IResourcePack> ordered = new ArrayList<>(packs);
        ordered.add(instance);
        return ordered;
    }

    private Path resolve(ResourceLocation location) { return root.resolve(location.getNamespace()).resolve(location.getPath()); }

    @Override @Nonnull public InputStream getInputStream(@Nonnull ResourceLocation location) throws IOException {
        Path file = resolve(location);
        if (!Files.isRegularFile(file)) { throw new FileNotFoundException(location.toString()); }
        return Files.newInputStream(file);
    }

    @Override public boolean resourceExists(@Nonnull ResourceLocation location) { return Files.isRegularFile(resolve(location)); }

    @Override @Nonnull public Set<String> getResourceDomains() {
        Set<String> domains = new HashSet<>();
        if (!Files.isDirectory(root)) { return domains; }
        try (Stream<Path> children = Files.list(root)) {
            children.filter(Files::isDirectory).map(path -> path.getFileName().toString()).forEach(domains::add);
        } catch (IOException e) { ICLogger.error("Could not list the overrides folder at " + root + ": " + e); }
        return domains;
    }

    @Override @Nullable public <T extends IMetadataSection> T getPackMetadata(@Nonnull MetadataSerializer serializer, @Nonnull String section) { return null; }

    @Override @Nonnull public BufferedImage getPackImage() throws IOException { throw new FileNotFoundException("pack.png"); }

    @Override @Nonnull public String getPackName() { return "Overrides folder"; }
}
