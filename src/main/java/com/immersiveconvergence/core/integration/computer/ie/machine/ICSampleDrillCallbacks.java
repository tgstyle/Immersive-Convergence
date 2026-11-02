package com.immersiveconvergence.core.integration.computer.ie.machine;

import blusunrize.immersiveengineering.api.excavator.ExcavatorHandler;
import blusunrize.immersiveengineering.common.blocks.metal.SampleDrillBlockEntity;
import blusunrize.immersiveengineering.common.items.CoresampleItem.VeinSampleData;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.owners.SampleDrillCallbacks;
import net.minecraft.locale.Language;
import javax.annotation.Nullable;
import java.util.List;

public class ICSampleDrillCallbacks extends SampleDrillCallbacks {
    @ComputerCallable public Object[] getVeinUnlocalizedName(CallbackEnvironment<SampleDrillBlockEntity> env) {
        VeinSampleData vein = finishedVein(env);
        return vein == null ? new Object[0] : new Object[] {name(vein)};
    }

    @ComputerCallable public Object[] getVeinLocalizedName(CallbackEnvironment<SampleDrillBlockEntity> env) {
        VeinSampleData vein = finishedVein(env);
        if (vein == null) { return new Object[0]; }
        return new Object[] {Language.getInstance().getOrDefault(vein.getType().getTranslationKey(), name(vein))};
    }

    @ComputerCallable public Object[] getVeinIntegrity(CallbackEnvironment<SampleDrillBlockEntity> env) {
        VeinSampleData vein = finishedVein(env);
        if (vein == null) { return new Object[0]; }
        int yield = ExcavatorHandler.mineralVeinYield;
        return new Object[] {yield == 0 ? -1.0F : 1.0F - vein.getDepletion() / (float) yield};
    }

    @ComputerCallable public int getVeinExpectedYield(CallbackEnvironment<SampleDrillBlockEntity> env) {
        List<VeinSampleData> veins = env.object().getVein();
        if (veins == null || veins.isEmpty()) { return -1; }
        return ExcavatorHandler.mineralVeinYield - veins.get(0).getDepletion();
    }

    private static String name(VeinSampleData vein) { return vein.getType().getId().toString(); }

    @Nullable private static VeinSampleData finishedVein(CallbackEnvironment<SampleDrillBlockEntity> env) {
        SampleDrillBlockEntity drill = env.object();
        if (!drill.isSamplingFinished()) { return null; }
        List<VeinSampleData> veins = drill.getVein();
        return veins == null || veins.isEmpty() ? null : veins.get(0);
    }
}
