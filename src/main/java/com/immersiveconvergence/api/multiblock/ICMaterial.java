package com.immersiveconvergence.api.multiblock;

import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public final class ICMaterial {
    private final ItemStack stack;
    private final String oreName;
    private final int count;

    private ICMaterial(ItemStack stack, String oreName, int count) {
        this.stack = stack;
        this.oreName = oreName;
        this.count = count;
    }

    public static ICMaterial of(ItemStack stack, int count) { return new ICMaterial(stack, null, count); }

    public static ICMaterial ore(String oreName, int count) { return new ICMaterial(ItemStack.EMPTY, oreName, count); }

    public ItemStack stack() { return stack; }

    @Nullable public String oreName() { return oreName; }

    public int count() { return count; }
}
