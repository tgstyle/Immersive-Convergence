package com.immersiveconvergence.mixin.ie;

import com.immersiveconvergence.api.util.ICUtils;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.metal.CapacitorBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.Nonnull;

@Mixin(CapacitorBlockEntity.class)
public abstract class CapacitorBlockEntityMixin implements IEBlockInterfaces.IPlayerInteraction {
    @Override @Nonnull public ItemInteractionResult interact(@Nonnull Direction side, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull ItemStack heldItem, float hitX, float hitY, float hitZ) {
        if (ICUtils.isHammer(heldItem)) {
            Direction activeSide = player.isShiftKeyDown() ? side.getOpposite() : side;
            if (((IEBlockInterfaces.IConfigurableSides) this).toggleSide(activeSide, player)) { return ItemInteractionResult.SUCCESS; }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
