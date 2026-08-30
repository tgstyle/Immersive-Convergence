package com.immersiveconvergence.api.client.split.geometry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class Polygon<Texture> {
    private static final EpsilonMath EPS_MATH = EpsilonMath.DEFAULT;
    private final List<Vertex> points;
    private final Texture texture;

    public Polygon(List<Vertex> points, Texture texture) {
        this.points = ImmutableList.copyOf(points);
        this.texture = texture;
    }

    public List<Vertex> getPoints() { return this.points; }

    public Texture getTexture() { return this.texture; }

    public Map<EpsilonMath.Sign, Polygon<Texture>> splitAlong(ModPlane p) {
        List<EpsilonMath.Sign> signs = new ArrayList<>(this.points.size());
        for (Vertex point : this.points) {
            double product = p.normal.dotProduct(point.position) - p.dotProduct;
            signs.add(EPS_MATH.sign(product));
        }

        int firstSignStart = 0;
        EpsilonMath.Sign zeroSign = signs.get(0);
        for (; firstSignStart < this.points.size(); firstSignStart++) {
            EpsilonMath.Sign signHere = signs.get(firstSignStart);
            if (zeroSign != signHere && signHere != EpsilonMath.Sign.ZERO) { break; }
        }

        if (firstSignStart >= this.points.size()) { return ImmutableMap.of(zeroSign, this); }
        EpsilonMath.Sign firstSign = signs.get(firstSignStart);
        EpsilonMath.Sign otherSign = firstSign.invert();
        if (!signs.contains(otherSign)) { return ImmutableMap.of(firstSign, this); }

        CyclicListWrapper<EpsilonMath.Sign> cyclicSigns = new CyclicListWrapper<>(signs);
        CyclicListWrapper<Vertex> cyclicPoints = new CyclicListWrapper<>(this.points);

        int otherSignStart = firstSignStart;
        while (cyclicSigns.get(otherSignStart) != otherSign) { otherSignStart++; }

        List<Vertex> firstInnerPoints = cyclicPoints.sublist(firstSignStart, otherSignStart);
        List<Vertex> otherInnerPoints = cyclicPoints.sublist(otherSignStart, firstSignStart);
        Vertex firstNewPoint = this.intersect(cyclicPoints.get(firstSignStart - 1), cyclicPoints.get(firstSignStart), p);
        Vertex otherNewPoint = this.intersect(cyclicPoints.get(otherSignStart - 1), cyclicPoints.get(otherSignStart), p);

        List<Vertex> poly1 = new ArrayList<>(firstInnerPoints.size() + 2);
        poly1.add(firstNewPoint);
        poly1.addAll(firstInnerPoints);
        poly1.add(otherNewPoint);

        List<Vertex> poly2 = new ArrayList<>(otherInnerPoints.size() + 2);
        poly2.add(otherNewPoint);
        poly2.addAll(otherInnerPoints);
        poly2.add(firstNewPoint);

        Map<EpsilonMath.Sign, Polygon<Texture>> result = new EnumMap<>(EpsilonMath.Sign.class);
        result.put(firstSign, new Polygon<>(poly1, this.texture));
        result.put(otherSign, new Polygon<>(poly2, this.texture));
        return result;
    }

    private Vertex intersect(Vertex a, Vertex b, ModPlane p) {
        double productA = a.position.dotProduct(p.normal);
        double productB = b.position.dotProduct(p.normal);
        double lambda = (p.dotProduct - productB) / (productA - productB);
        return Vertex.interpolate(a, b, lambda);
    }

    public Polygon<Texture> translate(int axis, double amount) {
        List<Vertex> translatedVertices = new ArrayList<>(this.points.size());
        for (Vertex v : this.points) { translatedVertices.add(v.translate(axis, amount)); }
        return new Polygon<>(translatedVertices, this.texture);
    }

    public Polygon<Texture> translate(Vec3d offset) {
        List<Vertex> translatedVertices = new ArrayList<>(this.points.size());
        for (Vertex v : this.points) { translatedVertices.add(v.translate(offset)); }
        return new Polygon<>(translatedVertices, this.texture);
    }

    public List<Polygon<Texture>> quadify() {
        List<Polygon<Texture>> quads = new ArrayList<>((this.points.size() / 2) + 1);
        int secondVertex;
        for (secondVertex = 1; secondVertex + 2 < this.points.size(); secondVertex += 2) {
            quads.add(new Polygon<>(ImmutableList.of(this.points.get(0), this.points.get(secondVertex), this.points.get(secondVertex + 1), this.points.get(secondVertex + 2)), this.texture));
        }
        if (secondVertex + 1 < this.points.size()) {
            quads.add(new Polygon<>(ImmutableList.of(this.points.get(0), this.points.get(secondVertex), this.points.get(secondVertex + 1), this.points.get(secondVertex + 1)), this.texture));
        }
        return quads;
    }
}
