package com.immersiveconvergence.client.models;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.Properties;

import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("deprecation") public final class ICObjIeBaked implements IBakedModel {
    private final OBJModel base;
    private final VertexFormat format;
    private final Function<ResourceLocation, TextureAtlasSprite> getter;
    private final IBakedModel plain;
    private final LoadingCache<List<String>, IBakedModel> cache;

    public ICObjIeBaked(@Nonnull OBJModel base, @Nonnull IModelState state, @Nonnull VertexFormat format, @Nonnull Function<ResourceLocation, TextureAtlasSprite> getter) {
        this.base = base;
        this.format = format;
        this.getter = getter;
        this.plain = base.bake(state, format, getter);
        this.cache = CacheBuilder.newBuilder().maximumSize(64).build(CacheLoader.from(g -> this.base.bake(new OBJModel.OBJState(g, true), this.format, this.getter)));
    }

    @Nonnull @Override public List<BakedQuad> getQuads(@Nullable IBlockState s, @Nullable EnumFacing side, long rand) {
        if (s instanceof IExtendedBlockState && ((IExtendedBlockState) s).getUnlistedNames().contains(Properties.AnimationProperty)) {
            IModelState v = ((IExtendedBlockState) s).getValue(Properties.AnimationProperty);
            if (v instanceof OBJModel.OBJState) return cache.getUnchecked(ImmutableList.copyOf(((OBJModel.OBJState) v).getGroupsWithVisibility(true))).getQuads(((IExtendedBlockState) s).getClean(), side, rand);
        }
        return plain.getQuads(s, side, rand);
    }

    @Override public boolean isAmbientOcclusion() { return plain.isAmbientOcclusion(); }

    @Override public boolean isAmbientOcclusion(@Nonnull IBlockState state) { return plain.isAmbientOcclusion(state); }

    @Override public boolean isGui3d() { return plain.isGui3d(); }

    @Override public boolean isBuiltInRenderer() { return plain.isBuiltInRenderer(); }

    @Nonnull @Override public TextureAtlasSprite getParticleTexture() { return plain.getParticleTexture(); }

    @SuppressWarnings("deprecation") @Nonnull @Override public ItemCameraTransforms getItemCameraTransforms() { return plain.getItemCameraTransforms(); }

    @Nonnull @Override public ItemOverrideList getOverrides() { return plain.getOverrides(); }

    @Nonnull @Override public Pair<? extends IBakedModel, Matrix4f> handlePerspective(@Nonnull ItemCameraTransforms.TransformType type) { return plain.handlePerspective(type); }
}
