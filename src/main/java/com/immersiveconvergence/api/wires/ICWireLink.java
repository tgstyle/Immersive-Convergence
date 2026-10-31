package com.immersiveconvergence.api.wires;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ICWireLink {
    private static final String TAG = "link";
    private final int dimension;
    private final ICConnectionPoint point;
    private final ICTargetingInfo target;

    public ICWireLink(int dimension, @Nonnull ICConnectionPoint point, @Nonnull ICTargetingInfo target) {
        this.dimension = dimension;
        this.point = point;
        this.target = target;
    }

    @Nullable public static ICWireLink read(@Nonnull ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey(TAG)) { return null; }
        NBTTagCompound link = tag.getCompoundTag(TAG);
        ICConnectionPoint point = new ICConnectionPoint(BlockPos.fromLong(link.getLong("pos")), link.getInteger("idx"));
        return new ICWireLink(link.getInteger("dim"), point, ICTargetingInfo.readFromNBT(link.getCompoundTag("target")));
    }

    public static void clear(@Nonnull ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null) { tag.removeTag(TAG); }
    }

    public void write(@Nonnull ItemStack stack) {
        NBTTagCompound link = new NBTTagCompound();
        link.setInteger("dim", dimension);
        link.setLong("pos", point.getPos().toLong());
        link.setInteger("idx", point.getIndex());
        NBTTagCompound targetTag = new NBTTagCompound();
        target.writeToNBT(targetTag);
        link.setTag("target", targetTag);
        stack.setTagInfo(TAG, link);
    }

    public int getDimension() { return dimension; }

    @Nonnull public ICConnectionPoint getPoint() { return point; }

    @Nonnull public ICTargetingInfo getTarget() { return target; }
}
