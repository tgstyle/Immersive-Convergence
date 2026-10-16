package com.immersiveconvergence.api.crafting.builders;

import com.immersiveconvergence.api.ICMods;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public final class ICIEContent {
    public static final ResourceLocation COKEBRICK = ICMods.ie("cokebrick");
    public static final ResourceLocation COMPONENT_IRON = ICMods.ie("component_iron");
    public static final ResourceLocation COMPONENT_STEEL = ICMods.ie("component_steel");
    public static final ResourceLocation CIRCUIT_BOARD = ICMods.ie("circuit_board");
    public static final ResourceLocation LOGIC_CIRCUIT = ICMods.ie("logic_circuit");
    public static final ResourceLocation SHEETMETAL_IRON = ICMods.ie("sheetmetal_iron");
    public static final ResourceLocation SHEETMETAL_STEEL = ICMods.ie("sheetmetal_steel");
    public static final ResourceLocation SLAB_SHEETMETAL_IRON = ICMods.ie("slab_sheetmetal_iron");
    public static final ResourceLocation SLAB_SHEETMETAL_STEEL = ICMods.ie("slab_sheetmetal_steel");
    public static final ResourceLocation FLUID_PUMP = ICMods.ie("fluid_pump");
    public static final ResourceLocation FLUID_PIPE = ICMods.ie("fluid_pipe");
    public static final ResourceLocation COIL_HV = ICMods.ie("coil_hv");
    public static final ResourceLocation CONNECTOR_REDSTONE = ICMods.ie("connector_redstone");
    public static final ResourceLocation CONNECTOR_HV = ICMods.ie("connector_hv");
    public static final ResourceLocation CONVEYOR_BASIC = ICMods.ie("conveyor_basic");
    public static final ResourceLocation FURNACE_HEATER = ICMods.ie("furnace_heater");

    private ICIEContent() {}

    public static Item item(ResourceLocation id) { return ForgeRegistries.ITEMS.getHolder(id).map(Holder::value).orElseThrow(() -> new IllegalArgumentException("Unknown item " + id)); }
}
