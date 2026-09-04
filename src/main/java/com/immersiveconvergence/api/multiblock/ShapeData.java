package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("unused")
public final class ShapeData {
    public final Function<BlockPos, VoxelShape> getter;
    public final int width, height, length;
    public final BlockPos masterPos, triggerPos;
    public final float manualScale;
    public final List<BlockPos> symmetricTriggerOffsets;

    private ShapeData(Function<BlockPos, VoxelShape> getter, int width, int height, int length, BlockPos masterPos, BlockPos triggerPos, float manualScale, List<BlockPos> symmetricTriggerOffsets) {
        this.getter = getter;
        this.width = width;
        this.height = height;
        this.length = length;
        this.masterPos = masterPos;
        this.triggerPos = triggerPos;
        this.manualScale = manualScale;
        this.symmetricTriggerOffsets = symmetricTriggerOffsets;
    }

    public static ShapeData load(Class<?> owner, String modid, String id) {
        MultiblockData data = MultiblockDataLoader.loadMultiblockData(owner, modid, id);
        int[] dims = GenericShape.loadDimensions(owner, modid, id);
        return fromData(data, id, dims[0], dims[1], dims[2]);
    }

    public static ShapeData fromData(MultiblockData data, String id, int width, int height, int length) {
        ICLib.IC_LOGGER.info("Loaded dimensions for {}: W={}, H={}, L={}", id, width, height, length);

        Function<BlockPos, VoxelShape> getter;
        if (width <= 0 || height <= 0 || length <= 0) {
            getter = FullblockShape.GETTER;
            width = height = length = 0;
            if (data.shapeAABB == null || !data.shapeAABB.isEmpty()) { ICLib.IC_LOGGER.error("Invalid dimensions loaded for {} multiblock.", id); }
        }
        else {
            int num = width * height * length;
            if (data.shapeAABB == null) {
                ICLib.IC_LOGGER.error("Failed to load shapes for {} multiblock. (shapeAABB null)", id);
                getter = FullblockShape.GETTER;
            }
            else if (data.shapeAABB.isEmpty()) {
                ICLib.IC_LOGGER.info("Using full block shape for {}.", id);
                getter = FullblockShape.GETTER;
            }
            else {
                List<List<AABB>> shapes = GenericShape.loadShapes(data, num);
                if (shapes == null) {
                    ICLib.IC_LOGGER.error("Failed to load shapes for {} multiblock.", id);
                    getter = FullblockShape.GETTER;
                }
                else {
                    boolean allFull = !shapes.isEmpty() && shapes.stream().allMatch(list -> list.size() == 1 && list.getFirst().equals(GenericShape.FULL_BLOCK));
                    getter = allFull ? FullblockShape.GETTER : new GenericShape.JsonShape(width, height, length, shapes);
                }
            }
        }

        BlockPos masterPos = null, triggerPos = null;
        List<BlockPos> symmetricTriggers = new ArrayList<>();
        if (data.pointsOfInterest != null) {
            for (PoIJSONSchema poi : data.pointsOfInterest) {
                switch (poi.name) {
                    case "master" -> masterPos = new BlockPos(poi.pos[0], poi.pos[1], poi.pos[2]);
                    case "trigger" -> triggerPos = new BlockPos(poi.pos[0], poi.pos[1], poi.pos[2]);
                    case "symmetric_trigger" -> symmetricTriggers.add(new BlockPos(poi.pos[0], poi.pos[1], poi.pos[2]));
                }
            }
        }

        return new ShapeData(getter, width, height, length, masterPos, triggerPos, data.manualScale, List.copyOf(symmetricTriggers));
    }
}
