package com.immersiveconvergence.api.fluid;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.bridge.ICBridges;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import javax.annotation.Nullable;

public interface IICFluidPipe {
    String NBT_PRESSURIZED = "pressurized";

    static boolean isPipe(@Nullable BlockEntity tile) { return ICMods.immersiveEngineering() && ICBridges.integration().isFluidPipe(tile); }

    static boolean is(@Nullable BlockEntity tile) { return tile instanceof IICFluidPipe || isPipe(tile); }

    static void refreshConnection(BlockEntity tile, Direction side) { if (ICMods.immersiveEngineering()) { ICBridges.integration().refreshFluidPipe(tile, side); } }

    static void markPressurized(FluidStack stack) { stack.getOrCreateTag().putBoolean(NBT_PRESSURIZED, true); }

    static void markIfPipe(FluidStack stack, @Nullable BlockEntity target) { if (isPipe(target)) { markPressurized(stack); } }

    static boolean isPressurized(FluidStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(NBT_PRESSURIZED);
    }

    static void stripPressure(FluidStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(NBT_PRESSURIZED)) { return; }
        tag.remove(NBT_PRESSURIZED);
        if (tag.isEmpty()) { stack.setTag(null); }
    }

    default boolean canOutputPressurized() { return false; }
}
