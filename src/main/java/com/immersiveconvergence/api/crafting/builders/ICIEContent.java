package com.immersiveconvergence.api.crafting.builders;

import com.immersiveconvergence.api.ICMods;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

@SuppressWarnings("unused") public final class ICIEContent {
    public static final ResourceLocation COKEBRICK = id("cokebrick");
    public static final ResourceLocation COMPONENT_IRON = id("component_iron");
    public static final ResourceLocation COMPONENT_STEEL = id("component_steel");
    public static final ResourceLocation CIRCUIT_BOARD = id("circuit_board");
    public static final ResourceLocation SHEETMETAL_IRON = id("sheetmetal_iron");
    public static final ResourceLocation SHEETMETAL_STEEL = id("sheetmetal_steel");
    public static final ResourceLocation SLAB_SHEETMETAL_IRON = id("slab_sheetmetal_iron");
    public static final ResourceLocation SLAB_SHEETMETAL_STEEL = id("slab_sheetmetal_steel");
    public static final ResourceLocation FLUID_PUMP = id("fluid_pump");
    public static final ResourceLocation FLUID_PIPE = id("fluid_pipe");
    public static final ResourceLocation COIL_HV = id("coil_hv");
    public static final ResourceLocation CONNECTOR_REDSTONE = id("connector_redstone");
    public static final ResourceLocation CONNECTOR_HV = id("connector_hv");
    public static final ResourceLocation CONVEYOR_BASIC = id("conveyor_basic");
    public static final ResourceLocation FURNACE_HEATER = id("furnace_heater");
    public static final ResourceLocation INGOT_HOP_GRAPHITE = id("ingot_hop_graphite");
    public static final ResourceLocation MOLD_PLATE = id("mold_plate");
    public static final ResourceLocation MOLD_ROD = id("mold_rod");
    public static final TagKey<Item> HAMMERS = ItemTags.create(id("tools/hammers"));

    private ICIEContent() {}

    private static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ICMods.IMMERSIVE_ENGINEERING, path); }

    public static Item item(ResourceLocation id) { return BuiltInRegistries.ITEM.get(id); }
}
