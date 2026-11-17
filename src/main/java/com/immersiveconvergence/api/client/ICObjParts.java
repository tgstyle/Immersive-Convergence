package com.immersiveconvergence.api.client;

import com.google.common.collect.UnmodifiableIterator;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.Models;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unused") public final class ICObjParts {
    public static final String BODY = "cube";
    private static final String PREFIX = BODY + "_";

    private ICObjParts() {}

    public static String part(String name) { return PREFIX + name; }

    public static IModelState only(Set<String> shown, IModelState rest) {
        return part -> {
            if (!part.isPresent() || part.get() instanceof ItemCameraTransforms.TransformType) { return rest.apply(part); }
            UnmodifiableIterator<String> path = Models.getParts(part.get());
            return path.hasNext() && !shown.contains(path.next()) ? Optional.of(TRSRTransformation.identity()) : Optional.empty();
        };
    }
}
