package com.immersiveconvergence.api.fluid;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.bridge.ICBridges;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import javax.annotation.Nullable;

public interface IICFluidPipe {
    int AMOUNT_PRESSURIZED = FluidType.BUCKET_VOLUME;
    int AMOUNT_UNPRESSURIZED = FluidType.BUCKET_VOLUME / 20;

    static boolean isPipe(@Nullable BlockEntity tile) { return ICMods.immersiveEngineering() && ICBridges.integration().isFluidPipe(tile); }

    static void refreshConnection(BlockEntity tile, Direction side) { if (ICMods.immersiveEngineering()) { ICBridges.integration().refreshFluidPipe(tile, side); } }

    static void markPressurized(FluidStack stack) { if (ICMods.immersiveEngineering()) { ICBridges.integration().markPressurized(stack); } }

    default boolean canOutputPressurized() { return false; }
}
