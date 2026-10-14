package com.immersiveconvergence.mixin;

import com.immersiveconvergence.common.ie.IMixerStateDebounce;

import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.mixer.MixerLogic;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MixerLogic.State.class)
public abstract class MixerLogicStateMixin implements IMixerStateDebounce {
    @Unique private final NonNullList<ItemStack> ic$lastComponents = NonNullList.withSize(MixerLogic.NUM_SLOTS, ItemStack.EMPTY);
    @Unique private int ic$stableTicks = 0;

    @Override public NonNullList<ItemStack> ic$getLastComponents() { return ic$lastComponents; }

    @Override public int ic$getStableTicks() { return ic$stableTicks; }

    @Override public void ic$setStableTicks(int ticks) { ic$stableTicks = ticks; }
}
