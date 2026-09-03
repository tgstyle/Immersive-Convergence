package com.immersiveconvergence.api.client.mirror;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class BakedMirroredModel implements IBakedModel {
    private final IBakedModel base;
    private final List<BakedQuad> unculled;
    private final Map<EnumFacing, List<BakedQuad>> culled = new EnumMap<>(EnumFacing.class);

    public BakedMirroredModel(IBakedModel base, EnumFacing.Axis axis) {
        this.base = base;
        this.unculled = mirrorAll(base.getQuads(null, null, 0), axis);
        for (EnumFacing side : EnumFacing.VALUES) { culled.put(side, mirrorAll(base.getQuads(null, mirror(side, axis), 0), axis)); }
    }

    public static EnumFacing.Axis axisFor(EnumFacing facing) { return facing.getAxis() == EnumFacing.Axis.Z ? EnumFacing.Axis.X : EnumFacing.Axis.Z; }

    private static EnumFacing mirror(EnumFacing side, EnumFacing.Axis axis) { return side.getAxis() == axis ? side.getOpposite() : side; }

    private static List<BakedQuad> mirrorAll(List<BakedQuad> quads, EnumFacing.Axis axis) {
        if (quads.isEmpty()) { return Collections.emptyList(); }
        List<BakedQuad> out = new ArrayList<>(quads.size());
        for (BakedQuad quad : quads) { out.add(mirror(quad, axis)); }
        return Collections.unmodifiableList(out);
    }

    public static BakedQuad mirror(BakedQuad quad, EnumFacing.Axis axis) {
        VertexFormat format = quad.getFormat();
        int[] in = quad.getVertexData();
        int stride = format.getIntegerSize();
        int vertices = in.length / stride;
        int[] out = new int[in.length];
        for (int v = 0; v < vertices; v++) { System.arraycopy(in, v * stride, out, (vertices - 1 - v) * stride, stride); }
        int component = axis.ordinal();
        for (int v = 0; v < vertices; v++) {
            for (int e = 0; e < format.getElementCount(); e++) {
                VertexFormatElement element = format.getElement(e);
                int at = v * stride + format.getOffset(e) / 4;
                if (element.getUsage() == VertexFormatElement.EnumUsage.POSITION && element.getType() == VertexFormatElement.EnumType.FLOAT) {
                    out[at + component] = Float.floatToRawIntBits(1.0F - Float.intBitsToFloat(out[at + component]));
                }
                else if (element.getUsage() == VertexFormatElement.EnumUsage.NORMAL && element.getType() == VertexFormatElement.EnumType.BYTE) {
                    int shift = component * 8;
                    int negated = (-(byte) (out[at] >> shift)) & 0xFF;
                    out[at] = (out[at] & ~(0xFF << shift)) | (negated << shift);
                }
            }
        }
        return new BakedQuad(out, quad.getTintIndex(), mirror(quad.getFace(), axis), quad.getSprite(), quad.shouldApplyDiffuseLighting(), format);
    }

    @Override @Nonnull public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) { return side == null ? unculled : culled.get(side); }

    @Override public boolean isAmbientOcclusion() { return base.isAmbientOcclusion(); }

    @Override public boolean isGui3d() { return base.isGui3d(); }

    @Override public boolean isBuiltInRenderer() { return base.isBuiltInRenderer(); }

    @Override @Nonnull public TextureAtlasSprite getParticleTexture() { return base.getParticleTexture(); }

    @SuppressWarnings("deprecation") @Override @Nonnull public ItemCameraTransforms getItemCameraTransforms() { return base.getItemCameraTransforms(); }

    @Override @Nonnull public ItemOverrideList getOverrides() { return base.getOverrides(); }
}
