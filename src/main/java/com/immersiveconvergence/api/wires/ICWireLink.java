package com.immersiveconvergence.api.wires;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public record ICWireLink(@Nonnull String dimension, @Nonnull ICConnectionPoint point) {
    private static final String TAG = "link";

    @Nullable public static ICWireLink read(@Nonnull ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(TAG)) { return null; }
        CompoundTag link = tag.getCompound(TAG);
        return new ICWireLink(link.getString("dim"), new ICConnectionPoint(BlockPos.of(link.getLong("pos")), link.getInt("idx")));
    }

    public static void clear(@Nonnull ItemStack stack) { stack.removeTagKey(TAG); }

    public void write(@Nonnull ItemStack stack) {
        CompoundTag link = new CompoundTag();
        link.putString("dim", dimension);
        link.putLong("pos", point.position().asLong());
        link.putInt("idx", point.index());
        stack.getOrCreateTag().put(TAG, link);
    }

    @Nonnull public String getDimension() { return dimension; }

    @Nonnull public ICConnectionPoint getPoint() { return point; }
}
