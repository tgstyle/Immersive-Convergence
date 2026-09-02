package com.immersiveconvergence.api.client.mirror;

import com.immersiveconvergence.api.client.BakedQuadUtils;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.model.ExtendedBlockModelDeserializer;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class MirrorModelLoader implements IGeometryLoader<MirrorGeometry> {
    public static final MirrorModelLoader INSTANCE = new MirrorModelLoader();
    public static final String INNER_MODEL = "inner_model";

    @Override @Nonnull public MirrorGeometry read(JsonObject modelContents, @Nonnull JsonDeserializationContext deserializationContext) throws JsonParseException {
        JsonElement innerJson = modelContents.get(INNER_MODEL);
        BlockModel baseModel = ExtendedBlockModelDeserializer.INSTANCE.fromJson(innerJson, BlockModel.class);
        return new MirrorGeometry(baseModel);
    }

    public static List<BakedQuad> reversedQuads(List<BakedQuad> quads) {
        if (quads.isEmpty()) { return ImmutableList.of(); }
        BakedQuad[] arr = new BakedQuad[quads.size()];
        for (int i = 0; i < quads.size(); i++) { arr[i] = BakedQuadUtils.reverseOrder(quads.get(i)); }
        return List.of(arr);
    }

    public static List<BakedQuad> getReversedQuads(SimpleBakedModel model, @Nullable Direction face) { return reversedQuads(model.getQuads(null, face, RandomSource.create(), ModelData.EMPTY, null)); }
}
