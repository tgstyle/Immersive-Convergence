package com.immersiveconvergence.api.fluid;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

@SuppressWarnings("unused")
public interface IICFluidPipe {
    boolean canOutputPressurized(boolean consumePower);

    boolean hasOutputConnection(EnumFacing side);

    static boolean is(TileEntity tile) {
        return tile instanceof IICFluidPipe || IEFluidPipeBridge.isIEPipe(tile);
    }

    static boolean canOutputPressurized(TileEntity tile, boolean consumePower) {
        if (tile instanceof IICFluidPipe) { return ((IICFluidPipe)tile).canOutputPressurized(consumePower); }
        return IEFluidPipeBridge.canOutputPressurized(tile, consumePower);
    }

    static boolean hasOutputConnection(TileEntity tile, EnumFacing side) {
        if (tile instanceof IICFluidPipe) { return ((IICFluidPipe)tile).hasOutputConnection(side); }
        return IEFluidPipeBridge.hasOutputConnection(tile, side);
    }
}
