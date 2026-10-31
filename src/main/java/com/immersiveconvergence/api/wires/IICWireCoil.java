package com.immersiveconvergence.api.wires;

import com.immersiveconvergence.core.registration.ICDataComponents;

import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IICWireCoil {
    @Nonnull ICWireType getWireType(@Nonnull ItemStack stack);

    default boolean canConnect(@Nonnull ItemStack stack, @Nonnull IICConnector connector, @Nonnull ICConnectionPoint point) { return connector.accepts(point, getWireType(stack)); }

    default int maxLength(@Nonnull ItemStack stack) { return getWireType(stack).getMaxLength(); }

    default int remaining(@Nonnull ItemStack stack) {
        Integer length = stack.get(ICDataComponents.COIL_LENGTH);
        return length != null ? length : maxLength(stack);
    }

    @Nonnull default ItemStack consume(@Nonnull ItemStack stack, int length) {
        ItemStack coil = stack.getCount() > 1 ? stack.split(1) : stack;
        int left = remaining(coil) - length;
        if (left <= 0) {
            coil.shrink(1);
            return ItemStack.EMPTY;
        }
        coil.set(ICDataComponents.COIL_LENGTH, left);
        return coil == stack ? ItemStack.EMPTY : coil;
    }

    @Nullable default ICWireLink link(@Nonnull ItemStack stack) { return ICWireLink.read(stack); }
}
