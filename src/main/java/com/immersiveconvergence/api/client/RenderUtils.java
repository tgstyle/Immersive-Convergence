package com.immersiveconvergence.api.client;

import com.immersiveconvergence.core.ICClientConfig;

import blusunrize.immersiveengineering.client.ClientUtils;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.vector.Vector3f;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class RenderUtils {
    private static final int LIGHT_REFRESH_TICKS = 4;
    private static final int LIGHT_CACHE_LIMIT = 256;
    private static final Map<Long, CachedLight> LIGHT_CACHE = new HashMap<>();
    private static final float[][] quadCoords = new float[4][3];
    private static final Vector3f side1 = new Vector3f();
    private static final Vector3f side2 = new Vector3f();
    private static final Vector3f normal = new Vector3f();
    private static CachedLight activeLight;

    public static void renderModelTESRFancy(List<BakedQuad> quads, BufferBuilder renderer, World world, BlockPos pos, boolean useCached) {
        if (ICClientConfig.rendering.disableFancyTESR) {
            ClientUtils.renderModelTESRFast(quads, renderer, world, pos);
            return;
        }
        CachedLight cached = useCached ? activeLight : null;
        if (cached == null) { cached = refreshLight(world, pos); }
        activeLight = cached;
        int localBrightness = world.getCombinedLight(pos, 0);
        for (BakedQuad quad : quads) {
            int[] vData = quad.getVertexData();
            VertexFormat format = quad.getFormat();
            int size = format.getIntegerSize();
            int uv = format.getUvOffsetById(0) / 4;
            for (int i = 0; i < 4; i++) {
                quadCoords[i][0] = Float.intBitsToFloat(vData[size * i]);
                quadCoords[i][1] = Float.intBitsToFloat(vData[size * i + 1]);
                quadCoords[i][2] = Float.intBitsToFloat(vData[size * i + 2]);
            }
            side1.x = quadCoords[1][0] - quadCoords[3][0];
            side1.y = quadCoords[1][1] - quadCoords[3][1];
            side1.z = quadCoords[1][2] - quadCoords[3][2];
            side2.x = quadCoords[2][0] - quadCoords[0][0];
            side2.y = quadCoords[2][1] - quadCoords[0][1];
            side2.z = quadCoords[2][2] - quadCoords[0][2];
            Vector3f.cross(side1, side2, normal);
            normal.normalise();
            int l1 = getLightValue(cached.brightness[0], cached.normalization[0], (localBrightness >> 16) & 255);
            int l2 = getLightValue(cached.brightness[1], cached.normalization[1], localBrightness & 255);
            for (int i = 0; i < 4; ++i) {
                renderer.pos(quadCoords[i][0], quadCoords[i][1], quadCoords[i][2])
                        .color(255, 255, 255, 255)
                        .tex(Float.intBitsToFloat(vData[size * i + uv]), Float.intBitsToFloat(vData[size * i + uv + 1]))
                        .lightmap(l1, l2)
                        .endVertex();
            }
        }
    }

    private static CachedLight refreshLight(World world, BlockPos pos) {
        long time = world.getTotalWorldTime();
        long key = pos.toLong();
        CachedLight cached = LIGHT_CACHE.get(key);
        if (cached == null) {
            if (LIGHT_CACHE.size() >= LIGHT_CACHE_LIMIT) { LIGHT_CACHE.clear(); }
            cached = new CachedLight();
            LIGHT_CACHE.put(key, cached);
        }
        else if (time - cached.lastUpdate < LIGHT_REFRESH_TICKS) { return cached; }
        cached.lastUpdate = time;
        for (EnumFacing f : EnumFacing.VALUES) {
            int val = world.getCombinedLight(pos.offset(f), 0);
            cached.brightness[0][f.getIndex()] = (val >> 16) & 255;
            cached.brightness[1][f.getIndex()] = val & 255;
        }
        for (int type = 0; type < 2; type++) for (int i = 0; i < 8; i++) { cached.normalization[type][i] = (float)Math.sqrt(computeSSquared(cached.brightness[type], i)); }
        return cached;
    }

    private static float computeSSquared(int[] brightness, int i) {
        float sSquared = 0;
        if ((i & 1) != 0) { sSquared += scaledSquared(brightness[5]); }
        else { sSquared += scaledSquared(brightness[4]); }
        if ((i & 2) != 0) { sSquared += scaledSquared(brightness[1]); }
        else { sSquared += scaledSquared(brightness[0]); }
        if ((i & 4) != 0) { sSquared += scaledSquared(brightness[3]); }
        else { sSquared += scaledSquared(brightness[2]); }
        return sSquared;
    }

    private static float scaledSquared(int val) { return (val / 255F) * (val / 255F); }

    private static int getLightValue(int[] neighbourBrightness, float[] normalizationFactors, int localBrightness) {
        float sideBrightness;
        byte type = 0;
        if (normal.x > 0) {
            sideBrightness = normal.x * neighbourBrightness[5];
            type |= 1;
        }
        else { sideBrightness = -normal.x * neighbourBrightness[4]; }
        if (normal.y > 0) {
            sideBrightness += normal.y * neighbourBrightness[1];
            type |= 2;
        }
        else { sideBrightness += -normal.y * neighbourBrightness[0]; }
        if (normal.z > 0) {
            sideBrightness += normal.z * neighbourBrightness[3];
            type |= 4;
        }
        else { sideBrightness += -normal.z * neighbourBrightness[2]; }
        return (int)((localBrightness + sideBrightness / normalizationFactors[type]) / 2);
    }

    private static final class CachedLight {
        final int[][] brightness = new int[2][6];
        final float[][] normalization = new float[2][8];
        long lastUpdate = Long.MIN_VALUE;
    }
}
