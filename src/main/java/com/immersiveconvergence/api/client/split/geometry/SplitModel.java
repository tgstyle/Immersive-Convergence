package com.immersiveconvergence.api.client.split.geometry;

import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class SplitModel<Texture> {
    private static final EpsilonMath EPS_MATH = EpsilonMath.DEFAULT;
    private final Map<ModelSplitterVec3i, SplitObjModel<Texture>> submodels;

    public SplitModel(SplitObjModel<Texture> input) {
        ImmutableMap.Builder<ModelSplitterVec3i, SplitObjModel<Texture>> builder = ImmutableMap.builder();
        for (Map.Entry<Integer, SplitObjModel<Texture>> xSlice : splitInPlanes(input, Axis.X).entrySet()) {
            for (Map.Entry<Integer, SplitObjModel<Texture>> zColumn : splitInPlanes(xSlice.getValue(), Axis.Z).entrySet()) {
                for (Map.Entry<Integer, SplitObjModel<Texture>> yDice : splitInPlanes(zColumn.getValue(), Axis.Y).entrySet()) {
                    builder.put(new ModelSplitterVec3i(xSlice.getKey(), yDice.getKey(), zColumn.getKey()), yDice.getValue());
                }
            }
        }
        this.submodels = builder.build();
    }

    public Map<ModelSplitterVec3i, SplitObjModel<Texture>> getParts() { return this.submodels; }

    private static <Texture> Map<Integer, SplitObjModel<Texture>> splitInPlanes(SplitObjModel<Texture> input, Axis axis) {
        if (input.isEmpty()) { return Collections.emptyMap(); }

        double min = axis.getMin(input);
        double max = axis.getMax(input);
        if (max - min < 1.0D) {
            Map<Integer, SplitObjModel<Texture>> result = new LinkedHashMap<>();
            putModel(result, axis, EPS_MATH.floor(min), input);
            return result;
        }

        int firstBorder = EPS_MATH.ceil(min);
        int lastBorder = EPS_MATH.floor(max);
        Map<Integer, SplitObjModel<Texture>> modelPerSection = new LinkedHashMap<>(lastBorder - firstBorder + 2);
        for (int borderPos = firstBorder; borderPos <= lastBorder; borderPos++) {
            ModPlane cut = new ModPlane(axis.getNormal(), borderPos);
            Map<EpsilonMath.Sign, SplitObjModel<Texture>> splitModel = input.split(cut);
            SplitObjModel<Texture> sectionModel = splitModel.get(EpsilonMath.Sign.NEGATIVE);
            putModel(modelPerSection, axis, borderPos - 1, sectionModel);
            input = SplitObjModel.union(splitModel.get(EpsilonMath.Sign.POSITIVE), splitModel.get(EpsilonMath.Sign.ZERO));
        }
        putModel(modelPerSection, axis, lastBorder, input);
        return modelPerSection;
    }

    private static <Texture> void putModel(Map<Integer, SplitObjModel<Texture>> sectionModels, Axis axis, int section, SplitObjModel<Texture> baseSectionModel) {
        if (baseSectionModel != null && !baseSectionModel.isEmpty()) { sectionModels.put(section, baseSectionModel.translate(axis.ordinal(), -section).quadify()); }
    }

    private enum Axis {
        X, Y, Z;

        public Vec3d getNormal() {
            double[] data = new double[3];
            data[this.ordinal()] = 1.0D;
            return new Vec3d(data);
        }

        public double getMin(SplitObjModel<?> m) {
            switch (this) {
                case X: return m.getMinX();
                case Y: return m.getMinY();
                default: return m.getMinZ();
            }
        }

        public double getMax(SplitObjModel<?> m) {
            switch (this) {
                case X: return m.getMaxX();
                case Y: return m.getMaxY();
                default: return m.getMaxZ();
            }
        }
    }
}
