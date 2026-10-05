package com.immersiveconvergence.common.util.compat.opencomputers;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.common.util.compat.ICCompatModule;

import li.cil.oc.api.Driver;

public class ICOpenComputers extends ICCompatModule {
    @Override public void preInit() {}

    @Override public void init() {
        if (!ICMods.immersiveEngineering()) { return; }
        Driver.add(new MetalPressDriver());
        Driver.add(new AutoWorkbenchDriver());
        Driver.add(new CokeOvenDriver());
        Driver.add(new BlastFurnaceDriver());
        Driver.add(new BlastFurnaceAdvancedDriver());
        Driver.add(new AlloySmelterDriver());
        Driver.add(new LightningRodDriver());
        Driver.add(new SheetmetalTankDriver());
        Driver.add(new SiloDriver());
        Driver.add(new FluidPumpDriver());
        Driver.add(new ClocheDriver());
        Driver.add(new ThermoelectricGeneratorDriver());
        Driver.add(new ChemicalThrowerTurretDriver());
        Driver.add(new GunTurretDriver());
        Driver.add(new BarrelDriver());
        Driver.add(new FluidPlacerDriver());
        Driver.add(new PreheaterDriver());
        Driver.add(new FurnaceHeaterDriver());
        if (!ICMods.immersivePetroleum()) { return; }
        Driver.add(new DistillationTowerDriver());
        Driver.add(new PumpjackDriver());
        Driver.add(new GasGeneratorDriver());
        Driver.add(new AutoLubricatorDriver());
    }
}
