package com.immersiveconvergence.common.util.compat.computers;

import com.immersiveconvergence.api.compat.ICComputerArgs;
import com.immersiveconvergence.api.compat.ICComputerTable;
import com.immersiveconvergence.mixin.ie.common.MixinIEComputerBelljar;
import com.immersiveconvergence.mixin.ie.common.MixinIEComputerThermoelectricGen;
import com.immersiveconvergence.mixin.ie.common.MixinIEComputerTurret;

import blusunrize.immersiveengineering.common.blocks.metal.TileEntityBelljar;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityBlastFurnacePreheater;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityFluidPlacer;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityFluidPump;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityFurnaceHeater;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityThermoelectricGen;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityTurret;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityTurretChem;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityTurretGun;
import blusunrize.immersiveengineering.common.blocks.wooden.TileEntityWoodenBarrel;

public final class IEDeviceComputerTables {
    public static final ICComputerTable<TileEntityFluidPump> FLUID_PUMP = IEComputerLocators.block("ie_fluid_pump", TileEntityFluidPump.class, te -> te.dummy ? 1 : 0)
            .add("getEnergyStored", te -> te.energyStorage.getEnergyStored())
            .add("getMaxEnergyStored", te -> te.energyStorage.getMaxEnergyStored())
            .add("getTankInfo", te -> te.tank.getInfo())
            .add("isPlacingCobble", te -> te.placeCobble);
    public static final ICComputerTable<TileEntityBelljar> CLOCHE = IEComputerLocators.block("ie_cloche", TileEntityBelljar.class, te -> te.dummy)
            .add("getEnergyStored", te -> te.energyStorage.getEnergyStored())
            .add("getMaxEnergyStored", te -> te.energyStorage.getMaxEnergyStored())
            .add("getTankInfo", te -> te.tank.getInfo())
            .add("getFertilizer", te -> te.fertilizerAmount)
            .add("getGrowth", te -> ((MixinIEComputerBelljar)te).getGrowth())
            .add("getSoilStack", te -> te.getInventory().get(TileEntityBelljar.SLOT_SOIL))
            .add("getSeedStack", te -> te.getInventory().get(TileEntityBelljar.SLOT_SEED))
            .call("getOutputStack", (te, args) -> new Object[] {te.getInventory().get(3 + ICComputerArgs.checkRange(args, 0, 4, "Output slots are 1-4"))});
    public static final ICComputerTable<TileEntityThermoelectricGen> THERMOELECTRIC_GENERATOR = IEComputerLocators.only("ie_thermoelectric_generator", TileEntityThermoelectricGen.class, te -> true)
            .add("getEnergyOutput", te -> Math.max(0, ((MixinIEComputerThermoelectricGen)te).getEnergyOutput()));
    public static final ICComputerTable<TileEntityTurretChem> TURRET_CHEM = turret(IEComputerLocators.block("ie_turret_chem", TileEntityTurretChem.class, te -> te.dummy ? 1 : 0))
            .add("getTankInfo", te -> te.tank.getInfo());
    public static final ICComputerTable<TileEntityTurretGun> TURRET_GUN = turret(IEComputerLocators.block("ie_turret_gun", TileEntityTurretGun.class, te -> te.dummy ? 1 : 0))
            .add("getAmmoStack", te -> te.getInventory().get(0))
            .add("getCasingStack", te -> te.getInventory().get(1));
    public static final ICComputerTable<TileEntityWoodenBarrel> BARREL = IEComputerLocators.only("ie_barrel", TileEntityWoodenBarrel.class, te -> true)
            .add("getTankInfo", te -> te.tank.getInfo());
    public static final ICComputerTable<TileEntityFluidPlacer> FLUID_PLACER = IEComputerLocators.only("ie_fluid_placer", TileEntityFluidPlacer.class, te -> true)
            .add("getTankInfo", te -> te.tank.getInfo());
    public static final ICComputerTable<TileEntityBlastFurnacePreheater> PREHEATER = IEComputerLocators.block("ie_blast_furnace_preheater", TileEntityBlastFurnacePreheater.class, te -> te.dummy)
            .add("getEnergyStored", te -> te.energyStorage.getEnergyStored())
            .add("getMaxEnergyStored", te -> te.energyStorage.getMaxEnergyStored())
            .add("isActive", te -> te.active);
    public static final ICComputerTable<TileEntityFurnaceHeater> FURNACE_HEATER = IEComputerLocators.only("ie_furnace_heater", TileEntityFurnaceHeater.class, te -> true)
            .add("getEnergyStored", te -> te.energyStorage.getEnergyStored())
            .add("getMaxEnergyStored", te -> te.energyStorage.getMaxEnergyStored())
            .add("isActive", te -> te.active);

    private IEDeviceComputerTables() {}

    private static <T extends TileEntityTurret> ICComputerTable<T> turret(ICComputerTable<T> table) {
        return table.add("getEnergyStored", te -> te.energyStorage.getEnergyStored())
                .add("getMaxEnergyStored", te -> te.energyStorage.getMaxEnergyStored())
                .add("isActive", te -> ((MixinIEComputerTurret)te).getTarget() != null);
    }
}
