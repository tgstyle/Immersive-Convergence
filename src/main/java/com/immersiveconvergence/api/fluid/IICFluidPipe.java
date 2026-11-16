package com.immersiveconvergence.api.fluid;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.bridge.ICBridges;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import javax.annotation.Nullable;

public interface IICFluidPipe {
    static boolean isPipe(@Nullable BlockEntity tile) { return ICMods.immersiveEngineering() && ICBridges.integration().isFluidPipe(tile); }

    static boolean is(@Nullable BlockEntity tile) { return tile instanceof IICFluidPipe || isPipe(tile); }

    static void refreshConnection(BlockEntity tile, Direction side) { if (ICMods.immersiveEngineering()) { ICBridges.integration().refreshFluidPipe(tile, side); } }

    static void markPressurized(FluidStack stack) { if (ICMods.immersiveEngineering()) { ICBridges.integration().markPressurized(stack); } }

    static boolean isPressurized(FluidStack stack) { return ICMods.immersiveEngineering() && ICBridges.integration().isPressurized(stack); }

    static void stripPressure(FluidStack stack) { if (ICMods.immersiveEngineering() && !stack.isEmpty()) { ICBridges.integration().stripPressure(stack); } }

    default boolean canOutputPressurized() { return false; }
}
