package com.immersiveconvergence.api.client.mirror;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

@SuppressWarnings({"unused", "RedundantSuppression"}) public record MirrorGeometry(UnbakedModel inner) implements IUnbakedGeometry<MirrorGeometry> {
    public BakedModel bake(IGeometryBakingContext owner, ModelBaker bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLoc) {
        BakedModel baseResult = inner.bake(bakery, spriteGetter, new MirrorModelState(modelState), modelLoc);
        return new MirrorBakedModel<>(baseResult);
    }
}
