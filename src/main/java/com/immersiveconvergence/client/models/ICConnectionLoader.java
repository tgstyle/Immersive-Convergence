package com.immersiveconvergence.client.models;

import com.google.common.collect.ImmutableMap;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ICConnectionLoader implements ICustomModelLoader {
    public static final ICConnectionLoader INSTANCE = new ICConnectionLoader();
    private static final String PREFIX = "models/block/smartmodel/conn_";
    private static final Map<String, ResourceLocation> BASES = new ConcurrentHashMap<>();

    private ICConnectionLoader() {}

    public static void register(@Nonnull String key, @Nonnull ResourceLocation baseModel) { BASES.put(key, baseModel); }

    @Override public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {}

    @Override public boolean accepts(@Nonnull ResourceLocation modelLocation) { return modelLocation.getPath().contains(PREFIX); }

    @Nonnull @Override public IModel loadModel(@Nonnull ResourceLocation modelLocation) throws Exception {
        String path = modelLocation.getPath();
        ResourceLocation base = BASES.get(path.substring(path.indexOf(PREFIX) + PREFIX.length()));
        if (base == null) { return ModelLoaderRegistry.getMissingModel(); }
        return new ICConnectionModel(ModelLoaderRegistry.getModel(base).process(ImmutableMap.of("flip-v", "true")));
    }
}
