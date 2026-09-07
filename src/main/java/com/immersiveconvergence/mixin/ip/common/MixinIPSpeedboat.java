package com.immersiveconvergence.mixin.ip.common;

import flaxbeard.immersivepetroleum.common.entity.EntitySpeedboat;
import flaxbeard.immersivepetroleum.common.items.ItemSpeedboat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntitySpeedboat.class)
public abstract class MixinIPSpeedboat {
    @Redirect(method = "updateFallState", at = @At(value = "INVOKE", target = "Lflaxbeard/immersivepetroleum/common/entity/EntitySpeedboat;dropItemWithOffset(Lnet/minecraft/item/Item;IF)Lnet/minecraft/entity/item/EntityItem;"))
    private EntityItem redirectFallDrop(EntitySpeedboat boat, Item dropped, int count, float offsetY) {
        if (!(dropped instanceof ItemSpeedboat)) { return boat.dropItemWithOffset(dropped, count, offsetY); }
        ItemSpeedboat item = (ItemSpeedboat)dropped;
        ItemStack stack = new ItemStack(item, 1, 0);
        NonNullList<ItemStack> contained = item.getContainedItems(stack);
        NonNullList<ItemStack> upgrades = boat.getUpgrades();
        for (int i = 0; i < contained.size() && i < upgrades.size(); i++) { contained.set(i, upgrades.get(i)); }
        stack.setTagCompound(new NBTTagCompound());
        item.setContainedItems(stack, contained);
        boat.writeTank(stack.getTagCompound(), true);
        return boat.entityDropItem(stack, offsetY);
    }
}
