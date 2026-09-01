package com.immersiveconvergence.data;

import com.immersiveconvergence.core.lib.ICLib;
import com.immersiveconvergence.core.registration.ICBlocks;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class ICLanguage extends LanguageProvider {
    public ICLanguage(PackOutput output) { super(output, ICLib.MODID, "en_us"); }

    @Override protected void addTranslations() {
        add("itemGroup." + ICLib.MODID, "Immersive Convergence");
        add(ICBlocks.ROTOR_CREATIVE.get(), "Creative Rotor");
        add(ICBlocks.HEAT_CREATIVE.get(), "Creative Heater");
        add("gui.immersiveconvergence.rotor_creative", "Creative Rotor");
        add("gui.immersiveconvergence.rotor_creative.rpm", "RPM");
        add("gui.immersiveconvergence.apply", "Apply");
    }
}
