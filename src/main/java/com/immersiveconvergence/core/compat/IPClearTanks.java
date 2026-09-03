package com.immersiveconvergence.core.compat;

import com.immersiveconvergence.api.multiblock.ClearTankRegistry;

import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.DerrickLogic;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.coker.CokerUnitLogic;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.distillation_tower.DistillationTowerLogic;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.hydro_treater.HydroTreaterLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class IPClearTanks {
    public static final String MODID = "immersivepetroleum";

    public static void register() {
        ClearTankRegistry.register(ipLoc("distillation_tower"), List.of(new BlockPos(3, 0, 3)), state -> IEClearTanks.empty(((DistillationTowerLogic.State) state).tanks.input()));
        ClearTankRegistry.register(ipLoc("hydrotreater"), List.of(new BlockPos(1, 0, 3), new BlockPos(2, 2, 1)), state -> IEClearTanks.empty(((HydroTreaterLogic.State) state).tanks.primary(), ((HydroTreaterLogic.State) state).tanks.secondary()));
        ClearTankRegistry.register(ipLoc("derrick"), List.of(new BlockPos(2, 0, 4)), state -> IEClearTanks.empty(((DerrickLogic.State) state).tank));
        ClearTankRegistry.register(ipLoc("coker_unit"), List.of(new BlockPos(2, 0, 4)), state -> IEClearTanks.empty(((CokerUnitLogic.State) state).bufferTanks.input()));
    }

    private static ResourceLocation ipLoc(String path) { return ResourceLocation.fromNamespaceAndPath(MODID, path); }
}
