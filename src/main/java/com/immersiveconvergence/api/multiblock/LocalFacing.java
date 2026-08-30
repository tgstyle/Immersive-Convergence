package com.immersiveconvergence.api.multiblock;

import com.google.gson.annotations.SerializedName;
import net.minecraft.util.EnumFacing;

public enum LocalFacing {
    @SerializedName("down")
    DOWN(0),
    @SerializedName("up")
    UP(1),
    @SerializedName("front")
    FRONT(2),
    @SerializedName("back")
    BACK(3),
    @SerializedName("left")
    LEFT(4),
    @SerializedName("right")
    RIGHT(5);

    private final int index;

    LocalFacing(int index) { this.index = index; }

    public EnumFacing LocalToGlobal(EnumFacing origin) {
        if(origin == null) return null;
        switch(this.index) {
            case 0: return RotateDown(origin);
            case 1: return RotateUp(origin);
            case 2: return origin;
            case 3: return origin.getOpposite();
            case 4: return origin.rotateYCCW();
            case 5: return origin.rotateY();
        }
        return null;
    }

    private EnumFacing RotateDown(EnumFacing origin) {
        switch(origin) {
            case UP: return EnumFacing.SOUTH;
            case DOWN: return EnumFacing.NORTH;
            default: return EnumFacing.DOWN;
        }
    }

    private EnumFacing RotateUp(EnumFacing origin) {
        switch(origin) {
            case UP: return EnumFacing.NORTH;
            case DOWN: return EnumFacing.SOUTH;
            default: return EnumFacing.UP;
        }
    }
}
