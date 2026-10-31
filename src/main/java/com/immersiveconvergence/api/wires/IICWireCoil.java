package com.immersiveconvergence.api.wires;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IICWireCoil {
    String LENGTH = "length";

    @Nonnull ICWireType getWireType(@Nonnull ItemStack stack);

    default boolean canConnect(@Nonnull ItemStack stack, @Nonnull IICConnector connector, @Nonnull ICConnectionPoint point) { return connector.accepts(point, getWireType(stack)); }

    default int maxLength(@Nonnull ItemStack stack) { return getWireType(stack).getMaxLength(); }

    default int remaining(@Nonnull ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.hasKey(LENGTH) ? tag.getInteger(LENGTH) : maxLength(stack);
    }

    @Nonnull default ItemStack consume(@Nonnull ItemStack stack, int length) {
        ItemStack coil = stack.getCount() > 1 ? stack.splitStack(1) : stack;
        int left = remaining(coil) - length;
        if (left <= 0) {
            coil.shrink(1);
            return ItemStack.EMPTY;
        }
        coil.setTagInfo(LENGTH, new NBTTagInt(left));
        return coil == stack ? ItemStack.EMPTY : coil;
    }

    @Nullable default ICWireLink link(@Nonnull ItemStack stack) { return ICWireLink.read(stack); }
}
