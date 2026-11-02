package com.immersiveconvergence.core.integration.computer.ie.machine;

import blusunrize.immersiveengineering.api.excavator.ExcavatorHandler;
import blusunrize.immersiveengineering.api.excavator.MineralMix;
import blusunrize.immersiveengineering.common.blocks.metal.SampleDrillBlockEntity;
import blusunrize.immersiveengineering.common.items.CoresampleItem.VeinSample;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.owners.SampleDrillCallbacks;
import net.minecraft.locale.Language;
import javax.annotation.Nullable;
import java.util.List;

public class ICSampleDrillCallbacks extends SampleDrillCallbacks {
    @ComputerCallable public Object[] getVeinUnlocalizedName(CallbackEnvironment<SampleDrillBlockEntity> env) {
        VeinSample vein = finishedVein(env);
        return vein == null ? new Object[0] : new Object[] {vein.mineral().toString()};
    }

    @ComputerCallable public Object[] getVeinLocalizedName(CallbackEnvironment<SampleDrillBlockEntity> env) {
        VeinSample vein = finishedVein(env);
        if (vein == null) { return new Object[0]; }
        String name = vein.mineral().toString();
        return new Object[] {Language.getInstance().getOrDefault(MineralMix.getTranslationKey(vein.mineral()), name)};
    }

    @ComputerCallable public Object[] getVeinIntegrity(CallbackEnvironment<SampleDrillBlockEntity> env) {
        VeinSample vein = finishedVein(env);
        if (vein == null) { return new Object[0]; }
        int yield = ExcavatorHandler.mineralVeinYield;
        return new Object[] {yield == 0 ? -1.0F : 1.0F - vein.depletion() / (float) yield};
    }

    @ComputerCallable public int getVeinExpectedYield(CallbackEnvironment<SampleDrillBlockEntity> env) {
        List<VeinSample> veins = env.object().getVein();
        if (veins == null || veins.isEmpty()) { return -1; }
        return ExcavatorHandler.mineralVeinYield - veins.getFirst().depletion();
    }

    @Nullable private static VeinSample finishedVein(CallbackEnvironment<SampleDrillBlockEntity> env) {
        SampleDrillBlockEntity drill = env.object();
        if (!drill.isSamplingFinished()) { return null; }
        List<VeinSample> veins = drill.getVein();
        return veins == null || veins.isEmpty() ? null : veins.getFirst();
    }
}
