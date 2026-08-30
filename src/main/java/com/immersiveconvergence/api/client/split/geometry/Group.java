package com.immersiveconvergence.api.client.split.geometry;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public final class Group<Texture> {
    private final List<Polygon<Texture>> faces;

    public Group(List<Polygon<Texture>> faces) { this.faces = ImmutableList.copyOf(faces); }

    public List<Polygon<Texture>> getFaces() { return this.faces; }

    public Map<EpsilonMath.Sign, Group<Texture>> split(ModPlane p) {
        Map<EpsilonMath.Sign, List<Polygon<Texture>>> splitFaces = new EnumMap<>(EpsilonMath.Sign.class);
        for (Polygon<Texture> f : this.faces) {
            for (Map.Entry<EpsilonMath.Sign, Polygon<Texture>> e : f.splitAlong(p).entrySet()) {
                splitFaces.computeIfAbsent(e.getKey(), k -> new ArrayList<>(4)).add(e.getValue());
            }
        }
        Map<EpsilonMath.Sign, Group<Texture>> result = new EnumMap<>(EpsilonMath.Sign.class);
        for (Map.Entry<EpsilonMath.Sign, List<Polygon<Texture>>> e : splitFaces.entrySet()) { result.put(e.getKey(), new Group<>(e.getValue())); }
        return result;
    }

    public Group<Texture> merge(Group<Texture> other) {
        ImmutableList.Builder<Polygon<Texture>> builder = ImmutableList.builder();
        builder.addAll(this.faces);
        builder.addAll(other.faces);
        return new Group<>(builder.build());
    }

    public Group<Texture> translate(int axis, double amount) {
        List<Polygon<Texture>> translated = new ArrayList<>(this.faces.size());
        for (Polygon<Texture> p : this.faces) { translated.add(p.translate(axis, amount)); }
        return new Group<>(translated);
    }

    public Group<Texture> translate(Vec3d offset) {
        List<Polygon<Texture>> translated = new ArrayList<>(this.faces.size());
        for (Polygon<Texture> p : this.faces) { translated.add(p.translate(offset)); }
        return new Group<>(translated);
    }
}
