package com.immersiveconvergence.core.compat.computer;

import com.immersiveconvergence.api.util.ComputerValues;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelper;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockLevel;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import flaxbeard.immersivepetroleum.api.reservoir.Reservoir;
import flaxbeard.immersivepetroleum.api.reservoir.ReservoirHandler;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.PumpjackLogic;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.PumpjackLogic.State;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.WellPipeTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.WellTileEntity;
import net.minecraft.server.level.ColumnPos;

@SuppressWarnings("unused") public class ReservoirCallbacks extends Callback<IMultiblockBEHelper<State>> {
    @ComputerCallable public String getReservoirFluid(CallbackEnvironment<IMultiblockBEHelper<State>> env) {
        Reservoir reservoir = reservoir(env);
        return reservoir == null ? null : ComputerValues.fluidName(reservoir.getFluid());
    }

    @ComputerCallable public int getReservoirAmount(CallbackEnvironment<IMultiblockBEHelper<State>> env) {
        Reservoir reservoir = reservoir(env);
        return reservoir == null ? 0 : (int) Math.min(reservoir.getAmount(), Integer.MAX_VALUE);
    }

    @ComputerCallable public int getReservoirReplenish(CallbackEnvironment<IMultiblockBEHelper<State>> env) {
        Reservoir reservoir = reservoir(env);
        return reservoir == null ? 0 : reservoir.getType().value().residual;
    }

    private static Reservoir reservoir(CallbackEnvironment<IMultiblockBEHelper<State>> env) {
        IMultiblockContext<State> context = env.object().getContext();
        if (context == null) { return null; }
        IMultiblockLevel level = context.getLevel();
        if (!(level.getBlockEntity(PumpjackLogic.DOWN_PORT.below()) instanceof WellPipeTileEntity pipe)) { return null; }
        WellTileEntity well = pipe.getWell();
        if (well == null) { return null; }
        for (ColumnPos pos : well.tappedReservoirs) {
            Reservoir reservoir = ReservoirHandler.getReservoir(level.getRawLevel(), pos);
            if (reservoir != null) { return reservoir; }
        }
        return null;
    }
}
