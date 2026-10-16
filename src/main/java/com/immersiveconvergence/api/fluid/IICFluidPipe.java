package com.immersiveconvergence.api.fluid;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.common.ie.IEFluidPipeBridge;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import javax.annotation.Nullable;

public interface IICFluidPipe {
    int AMOUNT_PRESSURIZED = FluidType.BUCKET_VOLUME;
    int AMOUNT_UNPRESSURIZED = FluidType.BUCKET_VOLUME / 20;
    String NBT_PRESSURIZED = "pressurized";

    static boolean isPipe(@Nullable BlockEntity tile) { return ICMods.immersiveEngineering() && IEFluidPipeBridge.isPipe(tile); }

    static void refreshConnection(BlockEntity tile, Direction side) { if (ICMods.immersiveEngineering()) { IEFluidPipeBridge.refreshConnection(tile, side); } }

    static void markPressurized(FluidStack stack) { stack.getOrCreateTag().putBoolean(NBT_PRESSURIZED, true); }

    static void markIfPipe(FluidStack stack, @Nullable BlockEntity target) { if (isPipe(target)) { markPressurized(stack); } }

    default boolean canOutputPressurized() { return false; }
}
