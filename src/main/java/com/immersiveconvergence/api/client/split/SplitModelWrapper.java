package com.immersiveconvergence.api.client.split;

import com.immersiveconvergence.api.client.split.geometry.Polygon;
import com.immersiveconvergence.api.client.split.geometry.SplitObjModel;
import com.immersiveconvergence.api.client.split.geometry.SplitModel;
import com.immersiveconvergence.api.client.split.geometry.ClumpedModel;
import com.immersiveconvergence.api.client.split.geometry.ModelSplitterVec3i;

import blusunrize.immersiveengineering.api.IEApi;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class SplitModelWrapper<T extends BakedModel> extends BakedModelWrapper<T> {
    @Nonnull private final Supplier<SplitData> splitDataSource;
    private volatile SplitData splitData;

    private static final Set<SplitModelWrapper<?>> WEAK_INSTANCES = Collections.newSetFromMap(new WeakHashMap<>());

    static {
        IEApi.renderCacheClearers.add(() -> WEAK_INSTANCES.forEach(SplitModelWrapper::clearCache));
    }

    protected SplitModelWrapper(T base, @NotNull Supplier<SplitData> splitData) {
        super(base);
        this.splitDataSource = splitData;
        WEAK_INSTANCES.add(this);
    }

    @Nullable protected SplitData splitData() {
        SplitData data = splitData;
        if (data == null) {
            data = splitDataSource.get();
            if (data != null) { splitData = data; }
        }
        return data;
    }

    @Override public boolean useAmbientOcclusion() { return false; }

    @Override @Nonnull public TriState useAmbientOcclusion(@Nonnull BlockState state, @Nonnull ModelData data, @Nonnull RenderType renderType) { return TriState.FALSE; }

    @Override @Nonnull public ModelData getModelData(@NotNull BlockAndTintGetter world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData tileData) {
        ModelData baseData = super.getModelData(world, pos, state, tileData);
        BlockEntity te = world.getBlockEntity(pos);
        SplitData data = splitData();
        Vec3i size = data == null ? Vec3i.ZERO : data.size();
        BlockPos offset = null;
        if (te instanceof ISubmodelOffsetProvider offsetProvider) { offset = offsetProvider.getModelOffset(state, size); }
        else if (state.getBlock() instanceof ISubmodelOffsetProvider offsetProvider) { offset = offsetProvider.getModelOffset(state, size); }
        if (offset != null) { return baseData.derive().with(SplitModelProperties.SUBMODEL_OFFSET, offset).build(); }
        else { return baseData; }
    }

    protected Map<Vec3i, List<BakedQuad>> split(List<BakedQuad> in, Set<Vec3i> parts, ModelState transform) {
        List<Polygon<QuadPolygonUtils.ExtraQuadData>> polys = in.stream().map(QuadPolygonUtils::toPolygon).collect(Collectors.toList());
        SplitObjModel<QuadPolygonUtils.ExtraQuadData> objModel = new SplitObjModel<>(polys);
        SplitModel<QuadPolygonUtils.ExtraQuadData> splitData = new SplitModel<>(objModel);
        Set<ModelSplitterVec3i> partsBMS = parts.stream().map(v -> new ModelSplitterVec3i(v.getX(), v.getY(), v.getZ())).collect(Collectors.toSet());
        ClumpedModel<QuadPolygonUtils.ExtraQuadData> clumpedModel = new ClumpedModel<>(splitData, partsBMS);

        Map<Vec3i, List<BakedQuad>> map = new HashMap<>();
        for (var e : clumpedModel.getClumpedParts().entrySet()) {
            List<BakedQuad> subModelFaces = new ArrayList<>(e.getValue().getFaces().size());
            for (Polygon<QuadPolygonUtils.ExtraQuadData> p : e.getValue().getFaces()) {
                subModelFaces.add(QuadPolygonUtils.toBakedQuad(p.getPoints(), p.getTexture(), transform.getRotation().blockCenterToCorner(), true));
            }
            Vec3i mcKey = new Vec3i(e.getKey().x(), e.getKey().y(), e.getKey().z());
            map.put(mcKey, subModelFaces);
        }
        return map;
    }

    protected void clearCache() { splitData = null; }
}
