package com.immersiveconvergence.api.client.split.geometry;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class ClumpedModel<Texture> {
    private final Map<ModelSplitterVec3i, SplitObjModel<Texture>> clumpedParts;

    public ClumpedModel(SplitModel<Texture> splitModel, Set<ModelSplitterVec3i> parts) {
        Preconditions.checkArgument(!parts.isEmpty());
        Map<ModelSplitterVec3i, SplitObjModel<Texture>> clumped = new HashMap<>();
        for (Map.Entry<ModelSplitterVec3i, SplitObjModel<Texture>> splitPart : splitModel.getParts().entrySet()) {
            ModelSplitterVec3i originalTarget = splitPart.getKey();
            ModelSplitterVec3i target = originalTarget;
            SplitObjModel<Texture> translatedModel = splitPart.getValue();
            if (!parts.contains(target)) {
                int optDist = Integer.MAX_VALUE;
                for (ModelSplitterVec3i candidate : parts) {
                    int currentDist = candidate.distanceSq(originalTarget);
                    if (currentDist < optDist) {
                        optDist = currentDist;
                        target = candidate;
                    }
                }
                ModelSplitterVec3i translatedBy = originalTarget.subtract(target);
                translatedModel = translatedModel.translate(new Vec3d(translatedBy));
            }
            clumped.merge(target, translatedModel, SplitObjModel::union);
        }
        this.clumpedParts = ImmutableMap.copyOf(clumped);
    }

    public Map<ModelSplitterVec3i, SplitObjModel<Texture>> getClumpedParts() { return this.clumpedParts; }
}
