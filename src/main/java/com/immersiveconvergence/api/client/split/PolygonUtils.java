package com.immersiveconvergence.api.client.split;

import com.immersiveconvergence.api.client.split.geometry.ClumpedModel;
import com.immersiveconvergence.api.client.split.geometry.ModelSplitterVec3i;
import com.immersiveconvergence.api.client.split.geometry.Polygon;
import com.immersiveconvergence.api.client.split.geometry.SplitModel;
import com.immersiveconvergence.api.client.split.geometry.SplitObjModel;
import com.immersiveconvergence.api.client.split.geometry.UVCoords;
import com.immersiveconvergence.api.client.split.geometry.Vec3d;
import com.immersiveconvergence.api.client.split.geometry.Vertex;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class PolygonUtils {
    private PolygonUtils() {}

    public static Map<BlockPos, List<BakedQuad>> split(List<BakedQuad> in, Set<BlockPos> parts) {
        List<Polygon<ExtraQuadData>> polys = new ArrayList<>(in.size());
        for (BakedQuad quad : in) { polys.add(toPolygon(quad)); }
        SplitModel<ExtraQuadData> splitData = new SplitModel<>(new SplitObjModel<>(polys));
        Set<ModelSplitterVec3i> partCells = new HashSet<>();
        for (BlockPos p : parts) { partCells.add(new ModelSplitterVec3i(p.getX(), p.getY(), p.getZ())); }
        ClumpedModel<ExtraQuadData> clumped = new ClumpedModel<>(splitData, partCells);
        Map<BlockPos, List<BakedQuad>> map = new HashMap<>();
        for (Map.Entry<ModelSplitterVec3i, SplitObjModel<ExtraQuadData>> e : clumped.getClumpedParts().entrySet()) {
            List<BakedQuad> quads = new ArrayList<>(e.getValue().getFaces().size());
            for (Polygon<ExtraQuadData> p : e.getValue().getFaces()) { quads.add(toBakedQuad(p.getPoints(), p.getTexture())); }
            map.put(new BlockPos(e.getKey().x, e.getKey().y, e.getKey().z), quads);
        }
        return map;
    }

    public static Polygon<ExtraQuadData> toPolygon(BakedQuad quad) {
        VertexFormat format = quad.getFormat();
        int posIdx = -1, uvIdx = -1, normalIdx = -1, colorIdx = -1;
        for (int e = 0; e < format.getElementCount(); e++) {
            VertexFormatElement el = format.getElement(e);
            if (el.getUsage() == VertexFormatElement.EnumUsage.POSITION) { posIdx = e; }
            else if (el.getUsage() == VertexFormatElement.EnumUsage.UV && el.getIndex() == 0) { uvIdx = e; }
            else if (el.getUsage() == VertexFormatElement.EnumUsage.NORMAL) { normalIdx = e; }
            else if (el.getUsage() == VertexFormatElement.EnumUsage.COLOR) { colorIdx = e; }
        }
        float[] data = new float[4];
        float[] color = new float[]{1F, 1F, 1F, 1F};
        if (colorIdx >= 0) { LightUtil.unpack(quad.getVertexData(), color, format, 0, colorIdx); }
        List<Vec3d> positions = new ArrayList<>(4);
        List<UVCoords> uvs = new ArrayList<>(4);
        List<Vec3d> normals = new ArrayList<>(4);
        for (int v = 0; v < 4; v++) {
            LightUtil.unpack(quad.getVertexData(), data, format, v, posIdx);
            positions.add(new Vec3d(data[0], data[1], data[2]));
            LightUtil.unpack(quad.getVertexData(), data, format, v, uvIdx);
            uvs.add(new UVCoords(data[0], data[1]));
            if (normalIdx >= 0) {
                LightUtil.unpack(quad.getVertexData(), data, format, v, normalIdx);
                normals.add(new Vec3d(data[0], data[1], data[2]).normalize());
            }
        }
        if (normals.isEmpty()) {
            Vec3d faceNormal = crossNormal(positions);
            for (int v = 0; v < 4; v++) { normals.add(faceNormal); }
        }
        List<Vertex> vertices = new ArrayList<>(4);
        for (int v = 0; v < 4; v++) { vertices.add(new Vertex(positions.get(v), normals.get(v), uvs.get(v))); }
        return new Polygon<>(vertices, new ExtraQuadData(format, quad.getSprite(), color.clone(), quad.getTintIndex(), quad.shouldApplyDiffuseLighting()));
    }

    public static BakedQuad toBakedQuad(List<Vertex> points, ExtraQuadData data) {
        VertexFormat format = data.format;
        UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
        builder.setTexture(data.sprite);
        builder.setQuadTint(data.tintIndex);
        builder.setApplyDiffuseLighting(data.diffuseLighting);
        Vec3d faceNormal = points.get(0).normal;
        builder.setQuadOrientation(EnumFacing.getFacingFromVector((float)faceNormal.x, (float)faceNormal.y, (float)faceNormal.z));
        for (Vertex v : points) {
            for (int e = 0; e < format.getElementCount(); e++) {
                VertexFormatElement el = format.getElement(e);
                switch (el.getUsage()) {
                    case POSITION:
                        builder.put(e, snap(v.position.x), snap(v.position.y), snap(v.position.z), 1F);
                        break;
                    case COLOR:
                        builder.put(e, data.color[0], data.color[1], data.color[2], data.color[3]);
                        break;
                    case UV:
                        if (el.getIndex() == 0) { builder.put(e, (float)v.uv.u, (float)v.uv.v, 0F, 1F); }
                        else { builder.put(e); }
                        break;
                    case NORMAL:
                        builder.put(e, (float)v.normal.x, (float)v.normal.y, (float)v.normal.z, 0F);
                        break;
                    default:
                        builder.put(e);
                        break;
                }
            }
        }
        return builder.build();
    }

    private static Vec3d crossNormal(List<Vec3d> positions) {
        Vec3d a = positions.get(1).subtract(positions.get(0));
        Vec3d b = positions.get(2).subtract(positions.get(0));
        return new Vec3d(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x).normalize();
    }

    private static float snap(double value) {
        double rounded = Math.round(value);
        return Math.abs(value - rounded) < 1.0E-5D ? (float)rounded : (float)value;
    }

    public static final class ExtraQuadData {
        public final VertexFormat format;
        public final TextureAtlasSprite sprite;
        public final float[] color;
        public final int tintIndex;
        public final boolean diffuseLighting;

        public ExtraQuadData(VertexFormat format, TextureAtlasSprite sprite, float[] color, int tintIndex, boolean diffuseLighting) {
            this.format = format;
            this.sprite = sprite;
            this.color = color;
            this.tintIndex = tintIndex;
            this.diffuseLighting = diffuseLighting;
        }
    }
}
