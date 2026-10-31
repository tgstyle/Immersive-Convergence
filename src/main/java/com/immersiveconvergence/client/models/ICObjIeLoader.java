package com.immersiveconvergence.client.models;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ICObjIeLoader implements ICustomModelLoader {
    public static final ICObjIeLoader INSTANCE = new ICObjIeLoader();
    private static final String EXTENSION = ".obj.ie";
    private static final Set<String> DOMAINS = ConcurrentHashMap.newKeySet();

    private ICObjIeLoader() {}

    public static void addDomain(@Nonnull String modid) { DOMAINS.add(modid.toLowerCase()); }

    @Override public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {}

    @Override public boolean accepts(@Nonnull ResourceLocation modelLocation) { return DOMAINS.contains(modelLocation.getNamespace()) && modelLocation.getPath().endsWith(EXTENSION); }

    @Nonnull @Override public IModel loadModel(@Nonnull ResourceLocation modelLocation) throws Exception { return new ICObjIeModel((OBJModel)OBJLoader.INSTANCE.loadModel(modelLocation)); }
}
