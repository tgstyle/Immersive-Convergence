package com.immersiveconvergence.api.client.split;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.property.IExtendedBlockState;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public class BakedSplitModel implements IBakedModel {
    private final IBakedModel base;
    private final Set<BlockPos> parts;
    private final Map<BlockPos, List<BakedQuad>>[] splitByLayer;

    @SuppressWarnings("unchecked") public BakedSplitModel(IBakedModel base, Set<BlockPos> parts) {
        this.base = base;
        this.parts = parts;
        this.splitByLayer = new Map[BlockRenderLayer.values().length + 1];
    }

    @Override @Nonnull public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (state == null) { return base.getQuads(null, side, rand); }
        if (side != null) { return ImmutableList.of(); }
        BlockPos offset = state instanceof IExtendedBlockState ? ((IExtendedBlockState)state).getValue(SplitModelProperties.SUBMODEL_OFFSET) : null;
        if (offset == null) { return ImmutableList.of(); }
        List<BakedQuad> quads = splitFor(MinecraftForgeClient.getRenderLayer()).get(offset);
        return quads == null ? ImmutableList.of() : quads;
    }

    private Map<BlockPos, List<BakedQuad>> splitFor(BlockRenderLayer layer) {
        int slot = layer == null ? 0 : layer.ordinal() + 1;
        Map<BlockPos, List<BakedQuad>> models = splitByLayer[slot];
        if (models == null) {
            synchronized (this) {
                models = splitByLayer[slot];
                if (models == null) {
                    List<BakedQuad> baseQuads = new ArrayList<>(base.getQuads(null, null, 0));
                    for (EnumFacing facing : EnumFacing.VALUES) { baseQuads.addAll(base.getQuads(null, facing, 0)); }
                    models = QuadPolygonUtils.split(baseQuads, parts);
                    splitByLayer[slot] = models;
                }
            }
        }
        return models;
    }

    @Override public boolean isAmbientOcclusion() { return false; }

    @Override public boolean isGui3d() { return base.isGui3d(); }

    @Override public boolean isBuiltInRenderer() { return false; }

    @Override @Nonnull public TextureAtlasSprite getParticleTexture() { return base.getParticleTexture(); }

    @SuppressWarnings("deprecation") @Override @Nonnull public ItemCameraTransforms getItemCameraTransforms() { return base.getItemCameraTransforms(); }

    @Override @Nonnull public ItemOverrideList getOverrides() { return base.getOverrides(); }
}
