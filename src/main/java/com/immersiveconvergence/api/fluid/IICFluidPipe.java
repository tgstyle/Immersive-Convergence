package com.immersiveconvergence.api.fluid;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;

@SuppressWarnings("unused") public interface IICFluidPipe {
    String NBT_PRESSURIZED = "pressurized";

    boolean canOutputPressurized(boolean consumePower);

    boolean hasOutputConnection(EnumFacing side);

    static boolean is(TileEntity tile) { return tile instanceof IICFluidPipe || IEFluidPipeBridge.isIEPipe(tile); }

    static void markPressurized(FluidStack stack) {
        if (stack.tag == null) { stack.tag = new NBTTagCompound(); }
        stack.tag.setBoolean(NBT_PRESSURIZED, true);
    }

    static boolean isPressurized(FluidStack stack) { return stack.tag != null && stack.tag.hasKey(NBT_PRESSURIZED); }

    static void stripPressure(FluidStack stack) {
        if (stack.tag == null || !stack.tag.hasKey(NBT_PRESSURIZED)) { return; }
        stack.tag.removeTag(NBT_PRESSURIZED);
        if (stack.tag.isEmpty()) { stack.tag = null; }
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
