package com.immersiveconvergence.mixin.ip.common;

import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import flaxbeard.immersivepetroleum.common.items.ItemProjector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.Nonnull;

@Mixin(ItemProjector.class)
public abstract class MixinIPProjectorAnchor extends Item {
    @Override @Nonnull public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, EntityPlayer player, @Nonnull EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!player.isSneaking() || !ItemNBTHelper.hasKey(stack, "pos")) { return new ActionResult<>(EnumActionResult.PASS, stack); }
        ItemNBTHelper.remove(stack, "pos");
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
