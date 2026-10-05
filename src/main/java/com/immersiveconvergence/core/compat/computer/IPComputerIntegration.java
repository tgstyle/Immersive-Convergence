package com.immersiveconvergence.core.compat.computer;

import com.immersiveconvergence.api.util.BlockComputerOwner;
import com.immersiveconvergence.api.util.ComputerCallbacks;

import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.PumpjackLogic;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.distillation_tower.DistillationTowerLogic;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.AutoLubricatorTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.GasGeneratorTileEntity;

public final class IPComputerIntegration {
    public static final String MODID = "immersivepetroleum";

    private IPComputerIntegration() {}

    public static void register() {
        ComputerCallbacks.registerMultiblock(IPContent.Multiblock.DISTILLATIONTOWER, new DistillationTowerCallbacks(), "distillation_tower", ComputerCallbacks.at(() -> DistillationTowerLogic.REDSTONE_IN));
        ComputerCallbacks.registerMultiblock(IPContent.Multiblock.PUMPJACK, new PumpjackCallbacks(), new ReservoirCallbacks(), "pumpjack", ComputerCallbacks.at(() -> PumpjackLogic.REDSTONE_IN));
        ComputerCallbacks.register(IPTileTypes.GENERATOR.getId(), new BlockComputerOwner<>(GasGeneratorTileEntity.class, "gas_generator", new GasGeneratorCallbacks()));
        ComputerCallbacks.register(IPTileTypes.AUTOLUBE.getId(), new BlockComputerOwner<>(AutoLubricatorTileEntity.class, "auto_lubricator", AutoLubricatorTileEntity::master, new AutoLubricatorCallbacks()));
    }
}
