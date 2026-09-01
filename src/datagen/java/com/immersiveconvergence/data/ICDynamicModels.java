package com.immersiveconvergence.data;

import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.client.model.generators.loaders.ObjModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ICDynamicModels extends BlockModelProvider {
    public ICDynamicModels(PackOutput output, ExistingFileHelper helper) { super(output, ICLib.MODID, helper); }

    @Override protected void registerModels() {
        rotor("dynamic/rotor_creative", "models/multiblock/metal/rotor_creative/rotor_creative.obj");
        rotor("dynamic/rotor_creative_east_west", "models/multiblock/metal/rotor_creative/rotor_creative_east_west.obj");
    }

    private void rotor(String name, String model) {
        getBuilder(name)
                .customLoader(ObjModelBuilder::begin)
                .modelLocation(modLoc(model))
                .flipV(true)
                .automaticCulling(false)
                .shadeQuads(true)
                .emissiveAmbient(true)
                .end()
                .texture("particle", modLoc("multiblock/metal/rotor_creative"))
                .renderType("cutout");
    }
}
