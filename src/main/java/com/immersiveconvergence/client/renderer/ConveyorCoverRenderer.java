package com.immersiveconvergence.client.renderer;

import blusunrize.immersiveengineering.api.tool.ConveyorHandler.ConveyorDirection;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.common.util.chickenbones.Matrix4;
import com.immersiveconvergence.api.util.ICUtils;
import com.immersiveconvergence.common.blocks.conveyors.ConveyorCoveredHelper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import org.lwjgl.util.vector.Vector3f;

public final class ConveyorCoverRenderer {
    private ConveyorCoverRenderer() {}

    private static final Map<IBlockState, CoverRenderData> coverRenderCache = new ConcurrentHashMap<>();
    private static final float[] COVER_COLOUR = {1.0F, 1.0F, 1.0F, 1.0F};
    private static final Matrix4[] ROTATION_MATRICES = new Matrix4[EnumFacing.values().length];
    private static final Function<Vector3f[], Vector3f[]> VERTEX_HORIZONTAL = v -> v;
    private static final Function<Vector3f[], Vector3f[]> VERTEX_UP = v -> {
        Vector3f[] ret = new Vector3f[v.length];
        for (int i = 0; i < v.length; ++i) { ret[i] = new Vector3f(v[i].x, v[i].y + (v[i].z == 0 ? 1 : 0), v[i].z); }
        return ret;
    };
    private static final Function<Vector3f[], Vector3f[]> VERTEX_DOWN = v -> {
        Vector3f[] ret = new Vector3f[v.length];
        for (int i = 0; i < v.length; ++i) { ret[i] = new Vector3f(v[i].x, v[i].y + (v[i].z == 1 ? 1 : 0), v[i].z); }
        return ret;
    };
    private static final Vector3f COVER_FROM = new Vector3f(0, 0.75F, 0);
    private static final Vector3f COVER_TO = new Vector3f(1, 1, 1);
    private static final Vector3f LEFT_FULL_FROM = new Vector3f(0, 0.1875F, 0);
    private static final Vector3f LEFT_FULL_TO = new Vector3f(0.0625F, 0.75F, 1);
    private static final Vector3f LEFT_POST1_FROM = new Vector3f(0, 0.1875F, 0);
    private static final Vector3f LEFT_POST1_TO = new Vector3f(0.0625F, 0.75F, 0.0625F);
    private static final Vector3f LEFT_POST2_FROM = new Vector3f(0, 0.1875F, 0.9375F);
    private static final Vector3f LEFT_POST2_TO = new Vector3f(0.0625F, 0.75F, 1);
    private static final Vector3f RIGHT_FULL_FROM = new Vector3f(0.9375F, 0.1875F, 0);
    private static final Vector3f RIGHT_FULL_TO = new Vector3f(1, 0.75F, 1);
    private static final Vector3f RIGHT_POST1_FROM = new Vector3f(0.9375F, 0.1875F, 0);
    private static final Vector3f RIGHT_POST1_TO = new Vector3f(1, 0.75F, 0.0625F);
    private static final Vector3f RIGHT_POST2_FROM = new Vector3f(0.9375F, 0.1875F, 0.9375F);
    private static final Vector3f RIGHT_POST2_TO = new Vector3f(1, 0.75F, 1);

    private static class CoverRenderData {
        final TextureAtlasSprite particle;
        final HashMap<EnumFacing, TextureAtlasSprite> faceSprites;

        CoverRenderData(TextureAtlasSprite particle, HashMap<EnumFacing, TextureAtlasSprite> faceSprites) {
            this.particle = particle;
            this.faceSprites = faceSprites;
        }
    }

    static {
        for (EnumFacing f : EnumFacing.values()) { ROTATION_MATRICES[f.ordinal()] = new Matrix4(f); }
    }

    private static CoverRenderData coverRenderData(IBlockState state) {
        return coverRenderCache.computeIfAbsent(state, key -> {
            IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(key);
            HashMap<EnumFacing, TextureAtlasSprite> sprites = new HashMap<>();
            for (EnumFacing f : EnumFacing.VALUES) {
                for (BakedQuad q : model.getQuads(key, f, 0L)) {
                    if (q != null) { sprites.put(f, q.getSprite()); }
                }
            }
            for (BakedQuad q : model.getQuads(key, null, 0L)) {
                if (q != null) { sprites.put(q.getFace(), q.getSprite()); }
            }
            return new CoverRenderData(model.getParticleTexture(), sprites);
        });
    }

    public static Function<EnumFacing, TextureAtlasSprite> coverSprites(IBlockState state) {
        CoverRenderData data = coverRenderData(state);
        return f -> data.faceSprites.getOrDefault(f, data.particle);
    }

    public static void addCoverToQuads(List<BakedQuad> baseModel, EnumFacing facing, Supplier<ItemStack> coverGet, ConveyorDirection conDir, boolean[] walls) {
        ItemStack coverStack = coverGet.get();
        ItemStack cover = coverStack.isEmpty() ? ConveyorCoveredHelper.defaultCover : coverStack;
        Block b = Block.getBlockFromItem(cover.getItem());
        IBlockState state = ICUtils.stateOf(b, cover.getMetadata());

        CoverRenderData renderData = coverRenderData(state);

        final TextureAtlasSprite particleSprite = renderData.particle;
        final HashMap<EnumFacing, TextureAtlasSprite> faceSprites = renderData.faceSprites;

        Function<EnumFacing, TextureAtlasSprite> getSprite = f -> faceSprites.getOrDefault(f, particleSprite);
        Function<EnumFacing, TextureAtlasSprite> getSpriteHorizontal = f -> f.getAxis() == Axis.Y ? null : faceSprites.getOrDefault(f, particleSprite);

        Matrix4 matrix = ROTATION_MATRICES[facing.ordinal()];
        Function<Vector3f[], Vector3f[]> vertexTransformer = conDir == ConveyorDirection.HORIZONTAL ? VERTEX_HORIZONTAL : (conDir == ConveyorDirection.UP ? VERTEX_UP : VERTEX_DOWN);

        baseModel.addAll(ClientUtils.createBakedBox(COVER_FROM, COVER_TO, matrix, facing, vertexTransformer, getSprite, COVER_COLOUR));

        if (walls[0]) { baseModel.addAll(ClientUtils.createBakedBox(LEFT_FULL_FROM, LEFT_FULL_TO, matrix, facing, vertexTransformer, getSpriteHorizontal, COVER_COLOUR)); }
        else {
            baseModel.addAll(ClientUtils.createBakedBox(LEFT_POST1_FROM, LEFT_POST1_TO, matrix, facing, vertexTransformer, getSpriteHorizontal, COVER_COLOUR));
            baseModel.addAll(ClientUtils.createBakedBox(LEFT_POST2_FROM, LEFT_POST2_TO, matrix, facing, vertexTransformer, getSpriteHorizontal, COVER_COLOUR));
        }
        if (walls[1]) { baseModel.addAll(ClientUtils.createBakedBox(RIGHT_FULL_FROM, RIGHT_FULL_TO, matrix, facing, vertexTransformer, getSpriteHorizontal, COVER_COLOUR)); }
        else {
            baseModel.addAll(ClientUtils.createBakedBox(RIGHT_POST1_FROM, RIGHT_POST1_TO, matrix, facing, vertexTransformer, getSpriteHorizontal, COVER_COLOUR));
            baseModel.addAll(ClientUtils.createBakedBox(RIGHT_POST2_FROM, RIGHT_POST2_TO, matrix, facing, vertexTransformer, getSpriteHorizontal, COVER_COLOUR));
        }
    }
}
