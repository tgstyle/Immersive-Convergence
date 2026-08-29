package com.immersiveconvergence.api.multiblock;

import com.google.gson.JsonArray;

@SuppressWarnings("unused")
public class MultiblockJSONSchema {
    public String uniqueName;
    public int width, height, length;
    public MasterJSONSchema master;
    public PoIJSONSchema[] pointsOfInterest;
    public BlockJSONSchema[] palette;
    public String[] structure;
    public JsonArray shapeAABB;
}
