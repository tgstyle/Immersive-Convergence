package com.immersiveconvergence.api.wires;

import com.immersiveconvergence.core.registration.ICDataComponents;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public record ICWireLink(@Nonnull String dimension, @Nonnull ICConnectionPoint point) {
    @Nullable public static ICWireLink read(@Nonnull ItemStack stack) {
        CompoundTag link = stack.get(ICDataComponents.COIL_LINK);
        if (link == null) { return null; }
        return new ICWireLink(link.getString("dim"), new ICConnectionPoint(BlockPos.of(link.getLong("pos")), link.getInt("idx")));
    }

    public static void clear(@Nonnull ItemStack stack) { stack.remove(ICDataComponents.COIL_LINK); }

    public void write(@Nonnull ItemStack stack) {
        CompoundTag link = new CompoundTag();
        link.putString("dim", dimension);
        link.putLong("pos", point.position().asLong());
        link.putInt("idx", point.index());
        stack.set(ICDataComponents.COIL_LINK, link);
    }

    @Nonnull public String getDimension() { return dimension; }

    @Nonnull public ICConnectionPoint getPoint() { return point; }
}
