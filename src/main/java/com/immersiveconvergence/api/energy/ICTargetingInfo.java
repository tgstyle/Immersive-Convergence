package com.immersiveconvergence.api.energy;

import blusunrize.immersiveengineering.api.TargetingInfo;
import net.minecraft.util.EnumFacing;

@SuppressWarnings("unused")
public class ICTargetingInfo {
    public final EnumFacing side;
    public final float hitX;
    public final float hitY;
    public final float hitZ;

    private ICTargetingInfo(TargetingInfo target) {
        this.side = target.side;
        this.hitX = target.hitX;
        this.hitY = target.hitY;
        this.hitZ = target.hitZ;
    }

    public static ICTargetingInfo of(TargetingInfo target) { return new ICTargetingInfo(target); }
}
