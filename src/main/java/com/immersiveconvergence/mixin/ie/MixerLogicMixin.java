package com.immersiveconvergence.mixin.ie;

import com.immersiveconvergence.common.ie.IMixerStateDebounce;

import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.mixer.MixerLogic;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.mixer.MixerLogic.State;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.lang.reflect.Field;

@Mixin(MixerLogic.class) public abstract class MixerLogicMixin {
    @Unique private static final int IC$STABLE_TICKS = 60;
    @Unique private static final Object IC$NOP;

    static {
        try {
            Field nopField = Class.forName("blusunrize.immersiveengineering.common.blocks.multiblocks.logic.mixer.MixerLogic$RecipeEnqueueState").getDeclaredField("NOP");
            nopField.setAccessible(true);
            IC$NOP = nopField.get(null);
        }
        catch (ReflectiveOperationException e) { throw new RuntimeException("Failed to access MixerLogic.RecipeEnqueueState.NOP", e); }
    }

    @Inject(method = "enqueueNewRecipes(Lblusunrize/immersiveengineering/common/blocks/multiblocks/logic/mixer/MixerLogic$State;Lnet/minecraft/world/level/Level;)Lblusunrize/immersiveengineering/common/blocks/multiblocks/logic/mixer/MixerLogic$RecipeEnqueueState;", at = @At("HEAD"), cancellable = true, remap = false) private void ic$smartDebounce(State state, Level rawLevel, CallbackInfoReturnable<Object> cir) {
        IMixerStateDebounce debounce = (IMixerStateDebounce) state;
        NonNullList<ItemStack> last = debounce.ic$getLastComponents();
        boolean playerChange = false;
        for (int i = 0; i < MixerLogic.NUM_SLOTS && !playerChange; ++i) {
            ItemStack current = state.inventory.getStackInSlot(i);
            ItemStack previous = last.get(i);
            playerChange = !ItemStack.isSameItemSameComponents(current, previous);
        }
        debounce.ic$setStableTicks(playerChange ? 0 : debounce.ic$getStableTicks() + 1);
        for (int i = 0; i < MixerLogic.NUM_SLOTS; ++i) {
            ItemStack stack = state.inventory.getStackInSlot(i);
            last.set(i, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
        }
        if (state.processor.getQueue().isEmpty() && debounce.ic$getStableTicks() < IC$STABLE_TICKS) { cir.setReturnValue(IC$NOP); }
    }
}
