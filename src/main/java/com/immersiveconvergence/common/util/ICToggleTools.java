package com.immersiveconvergence.common.util;

import com.immersiveconvergence.api.ICLib;
import com.immersiveconvergence.api.ICMods;

import blusunrize.immersiveengineering.common.items.ItemIETool;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

public final class ICToggleTools {
    private ICToggleTools() {}

    public static void register() {
        register(ICMods.IMMERSIVE_ENGINEERING, "tool", ItemIETool.HAMMER_META);
        register("immersivetech", "formation_tool", OreDictionary.WILDCARD_VALUE);
        register(ICMods.IMMERSIVE_FABRICATION, "screwdriver", OreDictionary.WILDCARD_VALUE);
    }

    private static void register(String modid, String name, int meta) {
        if (!Loader.isModLoaded(modid)) { return; }
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(modid, name));
        if (item != null && item != Items.AIR) { OreDictionary.registerOre(ICLib.ORE_TOOL_TOGGLE, new ItemStack(item, 1, meta)); }
    }
}
