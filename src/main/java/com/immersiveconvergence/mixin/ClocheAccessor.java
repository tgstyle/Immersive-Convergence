package com.immersiveconvergence.mixin;

import blusunrize.immersiveengineering.common.blocks.metal.ClocheBlockEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ClocheBlockEntity.class, remap = false)
public interface ClocheAccessor {
    @Accessor(value = "inventory", remap = false) NonNullList<ItemStack> ic$getInventory();
}
