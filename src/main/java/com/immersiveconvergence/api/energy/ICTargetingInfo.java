package com.immersiveconvergence.api.energy;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

@SuppressWarnings("unused")
public class ICTargetingInfo {
    public final EnumFacing side;
    public final float hitX;
    public final float hitY;
    public final float hitZ;

    public ICTargetingInfo(EnumFacing side, float hitX, float hitY, float hitZ) {
        this.side = side;
        this.hitX = hitX;
        this.hitY = hitY;
        this.hitZ = hitZ;
    }

    public void writeToNBT(NBTTagCompound tag) {
        tag.setInteger("side", side.ordinal());
        tag.setFloat("hitX", hitX);
        tag.setFloat("hitY", hitY);
        tag.setFloat("hitZ", hitZ);
    }

    public static ICTargetingInfo readFromNBT(NBTTagCompound tag) {
        return new ICTargetingInfo(EnumFacing.values()[tag.getInteger("side")], tag.getFloat("hitX"), tag.getFloat("hitY"), tag.getFloat("hitZ"));
    }
}
