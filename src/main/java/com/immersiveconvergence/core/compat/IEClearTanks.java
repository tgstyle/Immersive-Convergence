package com.immersiveconvergence.core.compat;

import com.immersiveconvergence.api.multiblock.ClearTankRegistry;

import blusunrize.immersiveengineering.api.IEApi;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.AssemblerLogic;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.DieselGeneratorLogic;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.RefineryLogic;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.bottling_machine.BottlingMachineLogic;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.mixer.MixerLogic;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

import java.util.List;

public class IEClearTanks {
    public static void register() {
        ClearTankRegistry.register(IEApi.ieLoc("diesel_generator"), List.of(new BlockPos(0, 0, 4), new BlockPos(2, 0, 4)), state -> empty(((DieselGeneratorLogic.State) state).tank));
        ClearTankRegistry.register(IEApi.ieLoc("assembler"), List.of(new BlockPos(1, 0, 2)), state -> empty(((AssemblerLogic.State) state).tanks));
        ClearTankRegistry.register(IEApi.ieLoc("refinery"), List.of(new BlockPos(0, 0, 1), new BlockPos(4, 0, 1)), state -> empty(((RefineryLogic.State) state).tanks.leftInput(), ((RefineryLogic.State) state).tanks.rightInput()));
        ClearTankRegistry.register(IEApi.ieLoc("mixer"), List.of(new BlockPos(0, 0, 1)), state -> {
            ((MixerLogic.State) state).tank.fluids.clear();
            return 1;
        });
        ClearTankRegistry.register(IEApi.ieLoc("bottling_machine"), List.of(new BlockPos(0, 0, 0)), state -> empty(((BottlingMachineLogic.State) state).tank));
    }

    public static int empty(IFluidTank... tanks) {
        for (IFluidTank tank : tanks) { tank.drain(Integer.MAX_VALUE, FluidAction.EXECUTE); }
        return tanks.length;
    }
}
