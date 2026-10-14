package com.immersiveconvergence.api.fluid;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.common.ie.IEFluidPipeBridge;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

import javax.annotation.Nullable;

@SuppressWarnings("unused") public interface IICFluidPipe {
    int AMOUNT_PRESSURIZED = FluidType.BUCKET_VOLUME;
    int AMOUNT_UNPRESSURIZED = FluidType.BUCKET_VOLUME / 20;
    String NBT_PRESSURIZED = "pressurized";

    static int getTransferableAmount(boolean pressurized) { return ICPipes.getTransferableAmount(pressurized); }

    static boolean isPipe(@Nullable BlockEntity tile) { return ICMods.immersiveEngineering() && IEFluidPipeBridge.isPipe(tile); }

    static void refreshConnection(BlockEntity tile, Direction side) { if (ICMods.immersiveEngineering()) { IEFluidPipeBridge.refreshConnection(tile, side); } }

    static void markPressurized(FluidStack stack) { if (ICMods.immersiveEngineering()) { IEFluidPipeBridge.markPressurized(stack); } }

    default boolean stripPressureTag() { return false; }

    default boolean canOutputPressurized(boolean consumePower) { return false; }
}
