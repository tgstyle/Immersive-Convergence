package com.immersiveconvergence.client.models;

import com.google.common.collect.ImmutableMap;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.model.IModelState;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.function.Function;

public final class ICObjIeModel implements IModel {
    private final OBJModel base;

    public ICObjIeModel(@Nonnull OBJModel base) { this.base = base; }

    @Nonnull @Override public Collection<ResourceLocation> getDependencies() { return base.getDependencies(); }

    @Nonnull @Override public Collection<ResourceLocation> getTextures() { return base.getTextures(); }

    @Nonnull @Override public IBakedModel bake(@Nonnull IModelState state, @Nonnull VertexFormat format, @Nonnull Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return new ICObjIeBaked(base, state, format, bakedTextureGetter);
    }

    @Nonnull @Override public IModelState getDefaultState() { return base.getDefaultState(); }

    @Nonnull @Override public IModel process(@Nonnull ImmutableMap<String, String> customData) { return new ICObjIeModel((OBJModel) base.process(customData)); }

    @Nonnull @Override public IModel retexture(@Nonnull ImmutableMap<String, String> textures) { return new ICObjIeModel((OBJModel) base.retexture(textures)); }
}
