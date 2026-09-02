package com.immersiveconvergence.api.client.mirror;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import javax.annotation.Nonnull;
import java.util.function.Function;

@SuppressWarnings({"unused", "RedundantSuppression"}) public record MirrorGeometry(UnbakedModel inner) implements IUnbakedGeometry<MirrorGeometry> {
    @Override @Nonnull public BakedModel bake(@Nonnull IGeometryBakingContext owner, @Nonnull ModelBaker bakery, @Nonnull Function<Material, TextureAtlasSprite> spriteGetter, @Nonnull ModelState modelState, @Nonnull ItemOverrides overrides) {
        BakedModel baseResult = inner.bake(bakery, spriteGetter, new MirrorModelState(modelState));
        return new MirrorBakedModel<>(baseResult);
    }
}
