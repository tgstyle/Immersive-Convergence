package com.immersiveconvergence.api.wires;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IICWireCoil {
    String LENGTH = "length";

    @Nonnull ICWireType getWireType(@Nonnull ItemStack stack);

    default boolean canConnect(@Nonnull ItemStack stack, @Nonnull IICConnector connector, @Nonnull ICConnectionPoint point) { return connector.accepts(point, getWireType(stack)); }

    default int maxLength(@Nonnull ItemStack stack) { return getWireType(stack).getMaxLength(); }

    default int remaining(@Nonnull ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(LENGTH) ? tag.getInt(LENGTH) : maxLength(stack);
    }

    @Nonnull default ItemStack consume(@Nonnull ItemStack stack, int length) {
        ItemStack coil = stack.getCount() > 1 ? stack.split(1) : stack;
        int left = remaining(coil) - length;
        if (left <= 0) {
            coil.shrink(1);
            return ItemStack.EMPTY;
        }
        coil.getOrCreateTag().putInt(LENGTH, left);
        return coil == stack ? ItemStack.EMPTY : coil;
    }

    @Nullable default ICWireLink link(@Nonnull ItemStack stack) { return ICWireLink.read(stack); }
}
