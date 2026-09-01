package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public abstract class GenericShape implements Function<BlockPos, VoxelShape> {
    public static final AABB FULL_BLOCK = new AABB(0D, 0D, 0D, 1D, 1D, 1D);
    private final Map<BlockPos, VoxelShape> shapeCache = new ConcurrentHashMap<>();

    public static int[] loadDimensions(Class<?> owner, String modid, String multiblockName) {
        String path = "/data/" + modid + "/structures/multiblocks/" + multiblockName + ".nbt";
        try (InputStream is = owner.getResourceAsStream(path)) {
            if (is == null) {
                ICLib.IC_LOGGER.error("Structure file not found at resource path: {} for multiblock: {}", path, multiblockName);
                return new int[]{0, 0, 0};
            }
            ListTag size = NbtIo.readCompressed(is).getList("size", Tag.TAG_INT);
            if (size.size() != 3) {
                ICLib.IC_LOGGER.error("Structure file {} has no usable size tag for multiblock: {}", path, multiblockName);
                return new int[]{0, 0, 0};
            }
            return new int[]{size.getInt(0), size.getInt(1), size.getInt(2)};
        }
        catch (Exception e) {
            ICLib.IC_LOGGER.error("Error reading structure file at {} for multiblock: {}", path, multiblockName, e);
            return new int[]{0, 0, 0};
        }
    }

    public static List<List<AABB>> loadShapes(MultiblockData data, int expectedNum) {
        if (data.shapeAABB == null) { return null; }
        List<List<AABB>> shapes = new ArrayList<>(expectedNum);
        for (JsonElement posElem : data.shapeAABB) {
            List<AABB> posShapes = new ArrayList<>();
            if (posElem.isJsonNull() || !posElem.isJsonArray()) { shapes.add(posShapes); continue; }
            JsonArray posArray = posElem.getAsJsonArray();
            if (posArray.isEmpty()) { posShapes.add(FULL_BLOCK); }
            for (JsonElement aabbElem : posArray) {
                if (!aabbElem.isJsonArray()) { continue; }
                JsonArray aabbArray = aabbElem.getAsJsonArray();
                if (aabbArray.size() != 6) { continue; }
                double[] vals = new double[6];
                for (int i = 0; i < 6; i++) { vals[i] = aabbArray.get(i).getAsDouble(); }
                posShapes.add(new AABB(vals[0], vals[1], vals[2], vals[3], vals[4], vals[5]));
            }
            shapes.add(posShapes);
        }
        if (shapes.size() != expectedNum) { return null; }
        return shapes;
    }

    @Override public VoxelShape apply(BlockPos posInMultiblock) { return shapeCache.computeIfAbsent(posInMultiblock.immutable(), this::buildShape); }

    private VoxelShape buildShape(BlockPos pos) {
        List<AABB> list = getShape(pos);
        if (list.isEmpty()) { return Shapes.empty(); }
        VoxelShape shape = Shapes.create(list.get(0));
        for (int i = 1; i < list.size(); i++) { shape = Shapes.or(shape, Shapes.create(list.get(i))); }
        return shape.optimize();
    }

    protected abstract List<AABB> getShape(BlockPos posInMultiblock);

    public static class JsonShape extends GenericShape {
        private final int WIDTH, HEIGHT, LENGTH;
        private final List<List<AABB>> SHAPES;

        public JsonShape(int width, int height, int length, List<List<AABB>> shapes) {
            this.WIDTH = width;
            this.HEIGHT = height;
            this.LENGTH = length;
            this.SHAPES = shapes;
        }

        @Override protected List<AABB> getShape(BlockPos posInMultiblock) {
            int x = posInMultiblock.getX();
            int y = posInMultiblock.getY();
            int z = posInMultiblock.getZ();
            if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT || z < 0 || z >= LENGTH) { return Collections.emptyList(); }
            int index = y * (WIDTH * LENGTH) + z * WIDTH + x;
            if (index < 0 || index >= SHAPES.size()) { return Collections.emptyList(); }
            return SHAPES.get(index);
        }
    }
}
