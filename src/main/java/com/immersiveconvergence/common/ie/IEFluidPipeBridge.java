package com.immersiveconvergence.common.ie;

import blusunrize.immersiveengineering.api.IEApiDataComponents;
import blusunrize.immersiveengineering.common.blocks.metal.FluidPipeBlockEntity;
import com.mojang.datafixers.util.Unit;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;

public final class IEFluidPipeBridge {
    private IEFluidPipeBridge() {}

    public static boolean isPipe(BlockEntity tile) { return tile instanceof FluidPipeBlockEntity; }

    public static void refreshConnection(BlockEntity tile, Direction side) {
        if (tile instanceof FluidPipeBlockEntity pipe) {
            pipe.updateConnectionByte(side);
            pipe.markContainingBlockForUpdate(null);
            pipe.setChanged();
        }
    }

    public static void markPressurized(FluidStack stack) { stack.set(IEApiDataComponents.FLUID_PRESSURIZED.get(), Unit.INSTANCE); }
}
