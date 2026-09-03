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
        add("desc.immersiveconvergence.info.holdShiftForInfo", "§8Hold Shift for more info§r");
        add("gui.immersiveconvergence.empty", "Empty");
        add("gui.immersiveconvergence.energy_stored", "%s/%s IF");
        add("gui.immersiveconvergence.fluid_amount", "%s mB");
        add("gui.immersiveconvergence.fluid_capacity", "%s/%s mB");
        add("gui.immersiveconvergence.fluid_density", "Density: %s");
        add("gui.immersiveconvergence.fluid_nbt", "NBT Data: %s");
        add("gui.immersiveconvergence.fluid_registry", "Fluid Registry: %s");
        add("gui.immersiveconvergence.fluid_temperature", "Temperature: %s");
        add("gui.immersiveconvergence.fluid_viscosity", "Viscosity: %s");
        add("gui.immersiveconvergence.fuel_empty", "Fuel: Empty");
        add("gui.immersiveconvergence.status", "Status: %s");
        add("gui.immersiveconvergence.status_active", "Active");
        add("gui.immersiveconvergence.status_inactive", "Inactive");
        add("gui.immersiveconvergence.input_tank_cleared", "Input tank cleared");
        add("gui.immersiveconvergence.input_tanks_cleared", "Input tanks cleared");
        add("config.jade.plugin_immersiveconvergence.status", "Machine Status");
        add("config.jade.plugin_immersiveconvergence.lines", "Machine Readouts");
    }
}
