package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.fluid.IICFluidPipe;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockContext;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;
import com.immersiveconvergence.api.multiblock.blocks.util.CapabilityPosition;
import com.immersiveconvergence.api.multiblock.blocks.util.MultiblockFace;
import com.immersiveconvergence.api.multiblock.blocks.util.RelativeBlockFace;
import com.immersiveconvergence.api.multiblock.util.CapabilityReference;
import com.immersiveconvergence.api.util.ICFluidUtils;
import com.immersiveconvergence.api.util.MarkableFluidTank;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

import java.util.List;

@SuppressWarnings({"unused", "RedundantSuppression"}) public interface IFluidOutputPump<State extends IMultiblockState> {
    List<BlockPos> getOutputPositions();
    Direction getOutputDirection(IMultiblockContext<State> ctx);
    List<MarkableFluidTank> getOutputTanks(State state);

    default List<RelativeBlockFace> getOutputFacings() { return null; }

    default int getTransferSpeed() { return Integer.MAX_VALUE; }

    default boolean shouldPumpOutputs(IMultiblockContext<State> ctx) { return true; }

    default boolean pumpOutputs(IMultiblockContext<State> ctx) {
        State state = ctx.getState();
        if (!shouldPumpOutputs(ctx)) return false;
        boolean dirty = false;
        Level level = ctx.getLevel().getRawLevel();
        List<BlockPos> outputPositions = getOutputPositions();
        Direction singleOutputDir = getOutputDirection(ctx);
        List<RelativeBlockFace> facings = getOutputFacings();
        List<MarkableFluidTank> tanks = getOutputTanks(state);
        for (int i = 0; i < tanks.size(); i++) {
            MarkableFluidTank tank = tanks.get(i);
            if (tank.getFluidAmount() == 0) continue;
            RelativeBlockFace face = (facings != null && !facings.isEmpty()) ? facings.get(i) : ctx.getLevel().toRelative(singleOutputDir);
            MultiblockFace mbf = new MultiblockFace(face, outputPositions.get(i));
            CapabilityPosition oppCp = CapabilityPosition.opposing(mbf);
            MultiblockFace oppMbf = new MultiblockFace(oppCp.side(), oppCp.posInMultiblock());
            CapabilityReference<IFluidHandler> ref = ctx.getCapabilityAt(ForgeCapabilities.FLUID_HANDLER, oppMbf);
            if (!ref.isPresent()) continue;
            IFluidHandler handler = ref.get();
            BlockPos portAbs = ctx.getLevel().toAbsolute(outputPositions.get(i));
            Direction outputDir = singleOutputDir;
            if (facings != null && !facings.isEmpty()) outputDir = ctx.getLevel().toAbsolute(facings.get(i));
            assert outputDir != null;
            BlockPos externalAbs = portAbs.relative(outputDir);
            BlockEntity adjTE = level.getBlockEntity(externalAbs);
            boolean isPipe = IICFluidPipe.isPipe(adjTE);
            FluidStack fs = tank.getFluid().copy();
            if (fs == null) continue;
            int transferSpeed = getTransferSpeed();
            if (transferSpeed != Integer.MAX_VALUE && !isPipe) fs = ICFluidUtils.copyFluidStackWithAmount(fs, Math.min(transferSpeed, fs.getAmount()));
            boolean hadTag = fs.hasTag() && fs.getTag().contains(IICFluidPipe.NBT_PRESSURIZED);
            if (isPipe && !hadTag) IICFluidPipe.markPressurized(fs);
            int accepted = handler.fill(fs, FluidAction.SIMULATE);
            if (!hadTag && fs.hasTag()) fs.getTag().remove(IICFluidPipe.NBT_PRESSURIZED);
            if (accepted <= 0) continue;
            FluidStack toFill = ICFluidUtils.copyFluidStackWithAmount(fs, Math.min(fs.getAmount(), accepted));
            if (isPipe) IICFluidPipe.markPressurized(toFill);
            int drained = handler.fill(toFill, FluidAction.EXECUTE);
            tank.drain(drained, FluidAction.EXECUTE);
            dirty = true;
        }
        if (dirty) ctx.markMasterDirty();
        return dirty;
    }

    default boolean isOutputConnected(IMultiblockContext<State> ctx, int index) {
        List<BlockPos> outputPositions = getOutputPositions();
        if (index < 0 || index >= outputPositions.size()) { return false; }
        List<RelativeBlockFace> facings = getOutputFacings();
        RelativeBlockFace face = (facings != null && !facings.isEmpty()) ? facings.get(index) : ctx.getLevel().toRelative(getOutputDirection(ctx));
        MultiblockFace mbf = new MultiblockFace(face, outputPositions.get(index));
        CapabilityPosition oppCp = CapabilityPosition.opposing(mbf);
        MultiblockFace oppMbf = new MultiblockFace(oppCp.side(), oppCp.posInMultiblock());
        CapabilityReference<IFluidHandler> ref = ctx.getCapabilityAt(ForgeCapabilities.FLUID_HANDLER, oppMbf);
        return ref.isPresent();
    }
}
