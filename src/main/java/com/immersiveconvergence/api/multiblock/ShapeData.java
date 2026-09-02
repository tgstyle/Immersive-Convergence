package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.common.util.ICLogger;
import com.immersiveconvergence.common.util.ICResources;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public final class ShapeData extends GenericShape {
    private static final Gson GSON = new Gson();
    public final MultiblockJSONSchema data;
    public final TemplateData template;
    public final BlockPos masterPos, triggerPos;
    public final List<BlockPos> triggerPositions;
    public final List<LocalFacing> triggerFacings;
    public final float manualScale;
    public final boolean hasShape;
    private final List<List<AxisAlignedBB>> shapes;

    private ShapeData(int width, int height, int length, MultiblockJSONSchema data, TemplateData template, BlockPos masterPos, List<BlockPos> triggerPositions, List<LocalFacing> triggerFacings, List<List<AxisAlignedBB>> shapes) {
        super(width, height, length);
        this.data = data;
        this.template = template;
        this.masterPos = masterPos;
        this.triggerPositions = triggerPositions;
        this.triggerFacings = triggerFacings;
        this.triggerPos = triggerPositions.get(0);
        this.manualScale = data != null ? data.manualScale : 0;
        this.hasShape = data != null && data.shapeAABB != null && data.shapeAABB.size() > 0;
        this.shapes = shapes;
    }

    public static ShapeData load(String modid, String id) {
        TemplateData template = TemplateData.load(modid, id);
        MultiblockJSONSchema data = loadJSON(modid, "multiblocks/" + id + ".json");
        if (template == null || data == null) {
            ICLogger.error("Missing multiblock data for " + id);
            return new ShapeData(0, 0, 0, data, template, BlockPos.ORIGIN, Collections.singletonList(BlockPos.ORIGIN), Collections.singletonList(null), new ArrayList<>());
        }

        int width = template.width, height = template.height, length = template.length;
        List<List<AxisAlignedBB>> shapes = new ArrayList<>();
        for (int i = 0; i < width * height * length; i++) { shapes.add(new ArrayList<>()); }

        if (data.shapeAABB != null) {
            int idx = 0;
            for (JsonElement posElem : data.shapeAABB) {
                if (idx >= shapes.size()) { break; }
                if (posElem.isJsonArray()) {
                    for (JsonElement aabbElem : posElem.getAsJsonArray()) {
                        if (!aabbElem.isJsonArray()) { continue; }
                        JsonArray box = aabbElem.getAsJsonArray();
                        if (box.size() == 6) { shapes.get(idx).add(new AxisAlignedBB(box.get(0).getAsDouble(), box.get(1).getAsDouble(), box.get(2).getAsDouble(), box.get(3).getAsDouble(), box.get(4).getAsDouble(), box.get(5).getAsDouble())); }
                    }
                }
                idx++;
            }
        }

        for (int y = 0; y < height; y++) {
            for (int z = 0; z < length; z++) {
                for (int x = 0; x < width; x++) {
                    if (template.getState(x, y, z) == null) { continue; }
                    List<AxisAlignedBB> posShapes = shapes.get(x + z * width + y * width * length);
                    if (posShapes.isEmpty()) { posShapes.add(new AxisAlignedBB(0, 0, 0, 1, 1, 1)); }
                }
            }
        }

        BlockPos masterPos = BlockPos.ORIGIN;
        List<BlockPos> triggerPositions = new ArrayList<>();
        List<LocalFacing> triggerFacings = new ArrayList<>();
        if (data.pointsOfInterest != null) {
            for (PoIJSONSchema poi : data.pointsOfInterest) {
                if (poi.pos == null || poi.pos.length != 3) { continue; }
                poi.position = new BlockPos(poi.pos[0], poi.pos[1], length - 1 - poi.pos[2]);
                if ("master".equals(poi.name)) { masterPos = poi.position; }
                else if ("trigger".equals(poi.name)) {
                    triggerPositions.add(poi.position);
                    triggerFacings.add(poi.facing);
                }
            }
        }
        if (triggerPositions.isEmpty()) {
            triggerPositions.add(masterPos);
            triggerFacings.add(null);
        }

        ICLogger.info(String.format("Loaded multiblock %s: %dx%dx%d, master at %s", id, width, height, length, masterPos));
        return new ShapeData(width, height, length, data, template, masterPos, triggerPositions, triggerFacings, shapes);
    }

    private static MultiblockJSONSchema loadJSON(String modid, String path) {
        try (JsonReader reader = new JsonReader(new InputStreamReader(ICResources.open(modid, path)))) {
            return GSON.fromJson(reader, MultiblockJSONSchema.class);
        }
        catch (JsonSyntaxException e) {
            ICLogger.error(String.format("Syntax error in file %s", path));
            return null;
        }
        catch (Exception e) {
            ICLogger.error(String.format("Couldn't load file %s", path));
            return null;
        }
    }

    @Override public List<AxisAlignedBB> getShape(BlockPos posInMultiblock) {
        int index = posInMultiblock.getX() + posInMultiblock.getZ() * width + posInMultiblock.getY() * width * length;
        return (index >= 0 && index < shapes.size()) ? shapes.get(index) : Collections.emptyList();
    }
}
