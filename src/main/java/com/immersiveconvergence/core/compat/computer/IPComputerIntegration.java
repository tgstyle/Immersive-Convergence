package com.immersiveconvergence.core.compat.computer;

import com.immersiveconvergence.api.compat.ICComputerControl;

import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.PumpjackLogic;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.distillation_tower.DistillationTowerLogic;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.AutoLubricatorTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.GasGeneratorTileEntity;

public final class IPComputerIntegration {
    private IPComputerIntegration() {}

    public static void register() {
        IEComputerCallbacks.registerMultiblock(IPContent.Multiblock.DISTILLATIONTOWER, new DistillationTowerCallbacks(), "distillation_tower", ICComputerControl.at(() -> DistillationTowerLogic.REDSTONE_IN));
        IEComputerCallbacks.registerMultiblock(IPContent.Multiblock.PUMPJACK, new PumpjackCallbacks(), new ReservoirCallbacks(), "pumpjack", ICComputerControl.at(() -> PumpjackLogic.REDSTONE_IN));
        IEComputerCallbacks.register(IPTileTypes.GENERATOR.getId(), new BlockComputerOwner<>(GasGeneratorTileEntity.class, "gas_generator", new GasGeneratorCallbacks()));
        IEComputerCallbacks.register(IPTileTypes.AUTOLUBE.getId(), new BlockComputerOwner<>(AutoLubricatorTileEntity.class, "auto_lubricator", AutoLubricatorTileEntity::master, new AutoLubricatorCallbacks()));
    }
}
