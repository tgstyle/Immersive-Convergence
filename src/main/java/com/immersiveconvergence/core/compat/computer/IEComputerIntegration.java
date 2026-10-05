package com.immersiveconvergence.core.compat.computer;

import com.immersiveconvergence.api.util.ActiveBlockCallbacks;
import com.immersiveconvergence.api.util.BlockComputerOwner;
import com.immersiveconvergence.api.util.ComputerCallbacks;
import com.immersiveconvergence.api.util.TankInfoCallbacks;

import blusunrize.immersiveengineering.common.blocks.MultiblockBEType;
import blusunrize.immersiveengineering.common.blocks.metal.BlastFurnacePreheaterBlockEntity;
import blusunrize.immersiveengineering.common.blocks.metal.ClocheBlockEntity;
import blusunrize.immersiveengineering.common.blocks.metal.FluidPlacerBlockEntity;
import blusunrize.immersiveengineering.common.blocks.metal.FluidPumpBlockEntity;
import blusunrize.immersiveengineering.common.blocks.metal.FurnaceHeaterBlockEntity;
import blusunrize.immersiveengineering.common.blocks.metal.MetalBarrelBlockEntity;
import blusunrize.immersiveengineering.common.blocks.metal.ThermoelectricGenBlockEntity;
import blusunrize.immersiveengineering.common.blocks.metal.TurretChemBlockEntity;
import blusunrize.immersiveengineering.common.blocks.metal.TurretGunBlockEntity;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.AdvBlastFurnaceLogic;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.AssemblerLogic;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.BlastFurnaceLogic;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.FermenterLogic;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.MetalPressLogic;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.RefineryLogic;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.SheetmetalTankLogic;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.SqueezerLogic;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.arcfurnace.ArcFurnaceLogic;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.mixer.MixerLogic;
import blusunrize.immersiveengineering.common.blocks.wooden.WoodenBarrelBlockEntity;
import blusunrize.immersiveengineering.common.register.IEBlockEntities;
import blusunrize.immersiveengineering.common.register.IEMultiblockLogic;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackOwner;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.owners.CapacitorCallbacks;

public final class IEComputerIntegration {
    private IEComputerIntegration() {}

    public static void register() {
        ComputerCallbacks.registerMultiblock(IEMultiblockLogic.METAL_PRESS, new MetalPressCallbacks(), "metal_press", ComputerCallbacks.at(() -> MetalPressLogic.REDSTONE_POS));
        ComputerCallbacks.registerMultiblock(IEMultiblockLogic.COKE_OVEN, new CokeOvenCallbacks(), new ActiveBlockCallbacks<>(), "coke_oven", ComputerCallbacks.anywhere());
        ComputerCallbacks.registerMultiblock(IEMultiblockLogic.BLAST_FURNACE, new BlastFurnaceCallbacks<>(BlastFurnaceLogic.State::getStateView), new ActiveBlockCallbacks<>(), "blast_furnace", ComputerCallbacks.anywhere());
        ComputerCallbacks.registerMultiblock(IEMultiblockLogic.ADV_BLAST_FURNACE, new BlastFurnaceCallbacks<>(AdvBlastFurnaceLogic.State::getStateView), new ActiveBlockCallbacks<>(), "advanced_blast_furnace", ComputerCallbacks.anywhere());
        ComputerCallbacks.registerMultiblock(IEMultiblockLogic.ALLOY_SMELTER, new AlloySmelterCallbacks(), new ActiveBlockCallbacks<>(), "alloy_smelter", ComputerCallbacks.anywhere());
        ComputerCallbacks.registerMultiblock(IEMultiblockLogic.LIGHTNING_ROD, new LightningRodCallbacks(), "lightning_rod", ComputerCallbacks.anywhere());
        ComputerCallbacks.registerMultiblock(IEMultiblockLogic.TANK, new TankInfoCallbacks<>(state -> state.tank, ""), "sheetmetal_tank", ComputerCallbacks.at(() -> SheetmetalTankLogic.IO_POS));
        ComputerCallbacks.register(IEBlockEntities.WOODEN_BARREL.getId(), new BlockComputerOwner<>(WoodenBarrelBlockEntity.class, "wooden_barrel", new TankInfoCallbacks<>(barrel -> barrel.tank, "")));
        ComputerCallbacks.register(IEBlockEntities.METAL_BARREL.getId(), new BlockComputerOwner<>(MetalBarrelBlockEntity.class, "metal_barrel", new TankInfoCallbacks<>(barrel -> barrel.tank, "")));
        ComputerCallbacks.register(IEBlockEntities.FLUID_PLACER.getId(), new BlockComputerOwner<>(FluidPlacerBlockEntity.class, "fluid_placer", new TankInfoCallbacks<>(placer -> placer.tank, "")));
        ComputerCallbacks.register(IEBlockEntities.THERMOELECTRIC_GEN.getId(), new BlockComputerOwner<>(ThermoelectricGenBlockEntity.class, "thermoelectric_generator", new ThermoelectricGenCallbacks()));
        ComputerCallbacks.register(IEBlockEntities.FURNACE_HEATER.getId(), new BlockComputerOwner<>(FurnaceHeaterBlockEntity.class, "furnace_heater", new FurnaceHeaterCallbacks()));
        registerDummied(IEBlockEntities.FLUID_PUMP, new BlockComputerOwner<>(FluidPumpBlockEntity.class, "fluid_pump", FluidPumpBlockEntity::master, new FluidPumpCallbacks()));
        registerDummied(IEBlockEntities.CLOCHE, new BlockComputerOwner<>(ClocheBlockEntity.class, "cloche", ClocheBlockEntity::master, new ClocheCallbacks()));
        registerDummied(IEBlockEntities.BLASTFURNACE_PREHEATER, new BlockComputerOwner<>(BlastFurnacePreheaterBlockEntity.class, "blast_furnace_preheater", BlastFurnacePreheaterBlockEntity::master, new PreheaterCallbacks()));
        registerDummied(IEBlockEntities.TURRET_CHEM, new BlockComputerOwner<>(TurretChemBlockEntity.class, "turret_chem", turret -> turret.master() instanceof TurretChemBlockEntity master ? master : null, new TurretChemCallbacks()));
        registerDummied(IEBlockEntities.TURRET_GUN, new BlockComputerOwner<>(TurretGunBlockEntity.class, "turret_gun", turret -> turret.master() instanceof TurretGunBlockEntity master ? master : null, new TurretGunCallbacks()));
        ComputerCallbacks.register(IEBlockEntities.CAPACITOR_CREATIVE.getId(), new CapacitorCallbacks("creative"));
        ComputerCallbacks.extendMultiblock(IEMultiblockLogic.ARC_FURNACE, new ICArcFurnaceCallbacks(), "arc_furnace", ArcFurnaceLogic.REDSTONE_POS);
        ComputerCallbacks.extendMultiblock(IEMultiblockLogic.FERMENTER, new ICFermenterCallbacks(), "fermenter", FermenterLogic.REDSTONE_POS);
        ComputerCallbacks.extendMultiblock(IEMultiblockLogic.SQUEEZER, new ICSqueezerCallbacks(), "squeezer", SqueezerLogic.REDSTONE_POS);
        ComputerCallbacks.extendMultiblock(IEMultiblockLogic.MIXER, new ICMixerCallbacks(), "mixer", MixerLogic.REDSTONE_POS);
        ComputerCallbacks.extendMultiblock(IEMultiblockLogic.REFINERY, new ICRefineryCallbacks(), "refinery", RefineryLogic.REDSTONE_POS);
        ComputerCallbacks.extendMultiblock(IEMultiblockLogic.ASSEMBLER, new ICAssemblerCallbacks(), "assembler", AssemblerLogic.REDSTONE_PORTS);
    }

    private static void registerDummied(MultiblockBEType<?> type, CallbackOwner<?> owner) {
        ComputerCallbacks.register(type.masterHolder().getId(), owner);
        ComputerCallbacks.register(type.dummyHolder().getId(), owner);
    }
}
