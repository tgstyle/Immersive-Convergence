package com.immersiveconvergence.api.client.split.geometry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class SplitObjModel<Texture> {
    private final Map<String, Group<Texture>> faces;
    private final List<Polygon<Texture>> allFaces;
    private final double minX, maxX, minY, maxY, minZ, maxZ;

    public SplitObjModel(List<Polygon<Texture>> allFaces) { this(ImmutableMap.of("", new Group<>(allFaces))); }

    public SplitObjModel(Map<String, Group<Texture>> faces) {
        this.faces = ImmutableMap.copyOf(faces);
        ImmutableList.Builder<Polygon<Texture>> builder = ImmutableList.builder();
        for (Group<Texture> g : faces.values()) { builder.addAll(g.getFaces()); }
        this.allFaces = builder.build();

        double mx = Double.POSITIVE_INFINITY, Mx = Double.NEGATIVE_INFINITY;
        double my = Double.POSITIVE_INFINITY, My = Double.NEGATIVE_INFINITY;
        double mz = Double.POSITIVE_INFINITY, Mz = Double.NEGATIVE_INFINITY;
        for (Polygon<Texture> p : this.allFaces) {
            for (Vertex v : p.getPoints()) {
                Vec3d pos = v.position;
                mx = Math.min(mx, pos.x);
                Mx = Math.max(Mx, pos.x);
                my = Math.min(my, pos.y);
                My = Math.max(My, pos.y);
                mz = Math.min(mz, pos.z);
                Mz = Math.max(Mz, pos.z);
            }
        }
        this.minX = mx; this.maxX = Mx;
        this.minY = my; this.maxY = My;
        this.minZ = mz; this.maxZ = Mz;
    }

    public static <Texture> SplitObjModel<Texture> union(SplitObjModel<Texture> a, SplitObjModel<Texture> b) {
        List<Polygon<Texture>> combined = new ArrayList<>();
        if (a != null) { combined.addAll(a.allFaces); }
        if (b != null) { combined.addAll(b.allFaces); }
        return new SplitObjModel<>(combined);
    }

    public Map<EpsilonMath.Sign, SplitObjModel<Texture>> split(ModPlane splitPlane) {
        Map<EpsilonMath.Sign, Group<Texture>> merged = new EnumMap<>(EpsilonMath.Sign.class);
        for (Group<Texture> g : this.faces.values()) {
            for (Map.Entry<EpsilonMath.Sign, Group<Texture>> e : g.split(splitPlane).entrySet()) { merged.merge(e.getKey(), e.getValue(), Group::merge); }
        }
        Map<EpsilonMath.Sign, SplitObjModel<Texture>> result = new EnumMap<>(EpsilonMath.Sign.class);
        for (Map.Entry<EpsilonMath.Sign, Group<Texture>> e : merged.entrySet()) { result.put(e.getKey(), new SplitObjModel<>(ImmutableMap.of("", e.getValue()))); }
        return result;
    }

    public SplitObjModel<Texture> translate(int axis, double amount) {
        List<Polygon<Texture>> translated = new ArrayList<>(this.allFaces.size());
        for (Polygon<Texture> p : this.allFaces) { translated.add(p.translate(axis, amount)); }
        return new SplitObjModel<>(translated);
    }

    public SplitObjModel<Texture> translate(Vec3d offset) {
        List<Polygon<Texture>> translated = new ArrayList<>(this.allFaces.size());
        for (Polygon<Texture> p : this.allFaces) { translated.add(p.translate(offset)); }
        return new SplitObjModel<>(translated);
    }

    public SplitObjModel<Texture> quadify() {
        List<Polygon<Texture>> quads = new ArrayList<>(this.allFaces.size());
        for (Polygon<Texture> p : this.allFaces) { quads.addAll(p.quadify()); }
        return new SplitObjModel<>(quads);
    }

    public boolean isEmpty() { return this.allFaces.isEmpty(); }

    public List<Polygon<Texture>> getFaces() { return this.allFaces; }

    public double getMinX() { return minX; }

    public double getMaxX() { return maxX; }

    public double getMinY() { return minY; }

    public double getMaxY() { return maxY; }

    public double getMinZ() { return minZ; }

    public double getMaxZ() { return maxZ; }
}
