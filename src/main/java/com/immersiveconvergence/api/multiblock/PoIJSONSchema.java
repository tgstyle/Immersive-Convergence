package com.immersiveconvergence.api.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class PoIJSONSchema {
    public String name;
    public int[] pos;
    @SerializedName("facing")
    public JsonElement facing;
    public List<RelativeBlockFace> relativeFaces = new ArrayList<>();
    public int[] origin;
    @SerializedName("origin_facing")
    public String originFacing;
    public int x;
}
