package com.immersiveconvergence.api.fluid;

import com.immersiveconvergence.api.ICMods;

import blusunrize.immersiveengineering.api.fluid.IFluidPipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

final class IEFluidPipeBridge {
    private IEFluidPipeBridge() {}

    static boolean isIEPipe(TileEntity tile) {
        if (!ICMods.immersiveEngineering()) { return false; }
        return tile instanceof IFluidPipe;
    }

    static boolean canOutputPressurized(TileEntity tile, boolean consumePower) {
        if (!ICMods.immersiveEngineering()) { return false; }
        return tile instanceof IFluidPipe && ((IFluidPipe)tile).canOutputPressurized(consumePower);
    }

    static boolean hasOutputConnection(TileEntity tile, EnumFacing side) {
        if (!ICMods.immersiveEngineering()) { return false; }
        return tile instanceof IFluidPipe && ((IFluidPipe)tile).hasOutputConnection(side);
    }
}
