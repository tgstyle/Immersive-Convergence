package com.immersiveconvergence.common.util.compat.jei;

import com.immersiveconvergence.api.jei.MultiblockIngredient;

import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.blocks.metal.BlockTypes_MetalMultiblock;
import blusunrize.immersiveengineering.common.blocks.stone.BlockTypes_StoneDevices;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

public final class EngineeringMultiblockIngredients {
    private static boolean registered = false;

    private EngineeringMultiblockIngredients() {}

    public static void register() {
        if (registered) { return; }
        registered = true;
        metal(BlockTypes_MetalMultiblock.METAL_PRESS, "ie.metalPress");
        metal(BlockTypes_MetalMultiblock.CRUSHER, "ie.crusher");
        metal(BlockTypes_MetalMultiblock.TANK);
        metal(BlockTypes_MetalMultiblock.SILO);
        metal(BlockTypes_MetalMultiblock.ASSEMBLER, "minecraft.crafting");
        metal(BlockTypes_MetalMultiblock.AUTO_WORKBENCH, "ie.workbench");
        metal(BlockTypes_MetalMultiblock.BOTTLING_MACHINE, "ie.bottlingMachine");
        metal(BlockTypes_MetalMultiblock.SQUEEZER, "ie.squeezer");
        metal(BlockTypes_MetalMultiblock.FERMENTER, "ie.fermenter");
        metal(BlockTypes_MetalMultiblock.REFINERY, "ie.refinery");
        metal(BlockTypes_MetalMultiblock.DIESEL_GENERATOR);
        metal(BlockTypes_MetalMultiblock.EXCAVATOR);
        metal(BlockTypes_MetalMultiblock.BUCKET_WHEEL);
        metal(BlockTypes_MetalMultiblock.ARC_FURNACE, "ie.arcFurnace", "ie.arcFurnace.recycling");
        metal(BlockTypes_MetalMultiblock.LIGHTNINGROD);
        metal(BlockTypes_MetalMultiblock.MIXER, "ie.mixer");
        stone(BlockTypes_StoneDevices.COKE_OVEN, "ie.cokeoven");
        stone(BlockTypes_StoneDevices.BLAST_FURNACE, "ie.blastfurnace", "ie.blastfurnace.fuel");
        stone(BlockTypes_StoneDevices.BLAST_FURNACE_ADVANCED, "ie.blastfurnace", "ie.blastfurnace.fuel");
        stone(BlockTypes_StoneDevices.ALLOY_SMELTER, "ie.alloysmelter");
        if (Loader.isModLoaded("immersivepetroleum")) { PetroleumMultiblockIngredients.register(); }
    }

    private static void metal(BlockTypes_MetalMultiblock type, String... catalystUids) { new MultiblockIngredient(new ItemStack(IEContent.blockMetalMultiblock, 1, type.getMeta()), catalystUids); }

    private static void stone(BlockTypes_StoneDevices type, String... catalystUids) { new MultiblockIngredient(new ItemStack(IEContent.blockStoneDevice, 1, type.getMeta()), catalystUids); }
}
