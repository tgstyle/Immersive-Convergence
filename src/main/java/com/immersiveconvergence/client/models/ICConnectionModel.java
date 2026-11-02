package com.immersiveconvergence.client.models;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.function.Function;

public final class ICConnectionModel implements IModel {
    public static final ResourceLocation WIRE_TEXTURE = new ResourceLocation("minecraft", "blocks/concrete_white");
    private final IModel base;

    public ICConnectionModel(@Nonnull IModel base) { this.base = base; }

    @Nonnull @Override public Collection<ResourceLocation> getDependencies() { return base.getDependencies(); }

    @Nonnull @Override public Collection<ResourceLocation> getTextures() { return ImmutableSet.<ResourceLocation>builder().addAll(base.getTextures()).add(WIRE_TEXTURE).build(); }

    @Nonnull @Override public IBakedModel bake(@Nonnull IModelState state, @Nonnull VertexFormat format, @Nonnull Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) { return new ICConnectionBaked(base.bake(state, format, bakedTextureGetter), format, bakedTextureGetter.apply(WIRE_TEXTURE)); }

    @Nonnull @Override public IModelState getDefaultState() { return base.getDefaultState(); }

    @Nonnull @Override public IModel process(@Nonnull ImmutableMap<String, String> customData) { return new ICConnectionModel(base.process(customData)); }

    @Nonnull @Override public IModel retexture(@Nonnull ImmutableMap<String, String> textures) { return new ICConnectionModel(base.retexture(textures)); }
}
