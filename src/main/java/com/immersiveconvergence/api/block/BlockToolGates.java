package com.immersiveconvergence.api.block;

import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class BlockToolGates {
    public static Predicate<ItemStack> isFormationTool = stack -> false;
    public static Predicate<ItemStack> isScrewdriver = stack -> false;
    public static String descFlavour = "";
    public static String descInfo = "";
}
