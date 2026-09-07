package com.immersiveconvergence.mixin.ip.common;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "flaxbeard.immersivepetroleum.common.SchematicCraftingHandler$SchematicResult")
public abstract class MixinIPSchematicResult {
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/NonNullList;set(ILjava/lang/Object;)Ljava/lang/Object;", remap = false), remap = false)
    private Object redirectGrowToGridSize(NonNullList<ItemStack> remaining, int p_set_1_, Object p_set_2_) {
        while (remaining.size() <= p_set_1_) { remaining.add(ItemStack.EMPTY); }
        return remaining.set(p_set_1_, (ItemStack) p_set_2_);
    }
}
