package com.immersiveconvergence.data;

import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ICItemModels extends ItemModelProvider {
    public ICItemModels(PackOutput output, ExistingFileHelper helper) { super(output, ICLib.MODID, helper); }

    @Override protected void registerModels() {
        generateBlockItem();
        generateBlockItem("rotor_creative", "dynamic/rotor_creative", false, -8, 4, 0.7f);
    }

    private void generateBlockItem() { generateBlockItem("heat_creative", "metal/heat_creative", true, 0, 0, 0.625f); }

    private void generateBlockItem(String itemName, String parentLocation, boolean useBlockPrefix, float guiTransX, float guiTransY, float guiScale) {
        String prefix = useBlockPrefix ? "block/" : "";
        ModelFile parentModel = new ModelFile.UncheckedModelFile(modLoc(prefix + parentLocation));
        getBuilder(itemName).parent(parentModel)
                .transforms()
                .transform(ItemDisplayContext.GUI).rotation(30, 225, 0).translation(guiTransX, guiTransY, 0).scale(guiScale, guiScale, guiScale).end()
                .transform(ItemDisplayContext.FIXED).rotation(0, 180, 0).scale(0.5f, 0.5f, 0.5f).end()
                .transform(ItemDisplayContext.GROUND).translation(0, 3, 0).scale(0.25f, 0.25f, 0.25f).end()
                .transform(ItemDisplayContext.HEAD).rotation(0, 180, 0).translation(0, 0, 0).scale(1f, 1f, 1f).end()
                .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND).rotation(0, 135, 0).scale(0.4f, 0.4f, 0.4f).end()
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).rotation(0, 315, 0).scale(0.4f, 0.4f, 0.4f).end()
                .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND).rotation(75, 45, 0).translation(0, 2.5f, 0).scale(0.375f, 0.375f, 0.375f).end()
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).rotation(75, 45, 0).translation(0, 2.5f, 0).scale(0.375f, 0.375f, 0.375f).end()
                .end();
    }
}
