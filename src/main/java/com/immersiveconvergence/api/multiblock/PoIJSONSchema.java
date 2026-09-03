package com.immersiveconvergence.api.multiblock;

import com.google.gson.JsonElement;
import net.minecraft.util.math.BlockPos;

public class PoIJSONSchema {
    public String name;
    public int[] pos;
    public JsonElement facing;
    public transient LocalFacing localFacing;
    public transient BlockPos position;
}
