package com.immersiveconvergence.client.models;

import com.immersiveconvergence.api.block.ICProperties;

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
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class ICConnectionBaked implements IBakedModel {
    private final IBakedModel base;
    private final LoadingCache<Set<?>, List<BakedQuad>> cache;

    public ICConnectionBaked(@Nonnull IBakedModel base, @Nonnull VertexFormat format, @Nonnull TextureAtlasSprite sprite) {
        this.base = base;
        this.cache = CacheBuilder.newBuilder().maximumSize(512).build(CacheLoader.from(ends -> ICWireGeometry.build(ends, format, sprite)));
    }

    @Nonnull @Override public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (MinecraftForgeClient.getRenderLayer() != BlockRenderLayer.SOLID) { return base.getQuads(state, side, rand); }
        List<BakedQuad> wires = side == null ? wireQuads(state) : Collections.emptyList();
        if (state != null && state.getBlock().canRenderInLayer(state, BlockRenderLayer.CUTOUT)) { return wires; }
        List<BakedQuad> own = base.getQuads(state, side, rand);
        if (wires.isEmpty()) { return own; }
        return ImmutableList.<BakedQuad>builder().addAll(own).addAll(wires).build();
    }

    private List<BakedQuad> wireQuads(@Nullable IBlockState state) {
        if (!(state instanceof IExtendedBlockState) || !((IExtendedBlockState)state).getUnlistedNames().contains(ICProperties.CONNECTIONS)) { return Collections.emptyList(); }
        Set<?> ends = ((IExtendedBlockState)state).getValue(ICProperties.CONNECTIONS);
        return ends == null || ends.isEmpty() ? Collections.emptyList() : cache.getUnchecked(ends);
    }

    @Override public boolean isAmbientOcclusion() { return base.isAmbientOcclusion(); }

    @Override public boolean isAmbientOcclusion(@Nonnull IBlockState state) { return base.isAmbientOcclusion(state); }

    @Override public boolean isGui3d() { return base.isGui3d(); }

    @Override public boolean isBuiltInRenderer() { return base.isBuiltInRenderer(); }

    @Nonnull @Override public TextureAtlasSprite getParticleTexture() { return base.getParticleTexture(); }

    @SuppressWarnings("deprecation") @Nonnull @Override public ItemCameraTransforms getItemCameraTransforms() { return base.getItemCameraTransforms(); }

    @Nonnull @Override public ItemOverrideList getOverrides() { return base.getOverrides(); }

    @Nonnull @Override public Pair<? extends IBakedModel, Matrix4f> handlePerspective(@Nonnull ItemCameraTransforms.TransformType type) { return Pair.of(this, base.handlePerspective(type).getRight()); }
}
