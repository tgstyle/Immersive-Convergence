package com.immersiveconvergence.client.render;

import com.immersiveconvergence.api.client.split.ParticleTextureHandler;

public final class IEParticleTextures {
    private static final String NAMESPACE = "immersiveengineering";

    private IEParticleTextures() {}

    public static void register() {
        ParticleTextureHandler.register(NAMESPACE, "cloth_device", "balloon", NAMESPACE + ":blocks/cloth_device_balloon");
        ParticleTextureHandler.register(NAMESPACE, "connector", "connector_hv", NAMESPACE + ":blocks/connector_connector_hv");
        ParticleTextureHandler.register(NAMESPACE, "connector", "connector_lv", NAMESPACE + ":blocks/connector_connector_lv");
        ParticleTextureHandler.register(NAMESPACE, "connector", "connector_mv", NAMESPACE + ":blocks/connector_connector_mv");
        ParticleTextureHandler.register(NAMESPACE, "connector", "connector_probe", NAMESPACE + ":blocks/connector_connector_probe");
        ParticleTextureHandler.register(NAMESPACE, "connector", "connector_redstone", NAMESPACE + ":blocks/connector_connector_redstone");
        ParticleTextureHandler.register(NAMESPACE, "connector", "connector_structural", NAMESPACE + ":blocks/connector_connector_structural");
        ParticleTextureHandler.register(NAMESPACE, "connector", "relay_hv", NAMESPACE + ":blocks/connector_relay_hv");
        ParticleTextureHandler.register(NAMESPACE, "connector", "relay_lv", NAMESPACE + ":blocks/connector_relay_lv");
        ParticleTextureHandler.register(NAMESPACE, "connector", "relay_mv", NAMESPACE + ":blocks/connector_relay_mv");
        ParticleTextureHandler.register(NAMESPACE, "connector_breaker_switch", "breakerswitch", NAMESPACE + ":blocks/connector_breaker_switch");
        ParticleTextureHandler.register(NAMESPACE, "connector_energy_meter", "energy_meter", NAMESPACE + ":blocks/connector_energy_meter");
        ParticleTextureHandler.register(NAMESPACE, "connector_redstone_breaker", "redstone_breaker", NAMESPACE + ":blocks/connector_redstone_breaker");
        ParticleTextureHandler.register(NAMESPACE, "connector_transformer", "transformer", NAMESPACE + ":blocks/connector_transformer");
        ParticleTextureHandler.register(NAMESPACE, "connector_transformer_hv", "transformer_hv", NAMESPACE + ":blocks/connector_transformer_hv");
        ParticleTextureHandler.register(NAMESPACE, "metal_decoration2", "alu_slope", NAMESPACE + ":blocks/metal_decoration1_aluminum_scaffolding_noctm");
        ParticleTextureHandler.register(NAMESPACE, "metal_decoration2", "aluminum_post", NAMESPACE + ":blocks/metal_decoration2_aluminum_post");
        ParticleTextureHandler.register(NAMESPACE, "metal_decoration2", "aluminum_wallmount", NAMESPACE + ":blocks/metal_decoration2_aluminum_wallmount");
        ParticleTextureHandler.register(NAMESPACE, "metal_decoration2", "lantern", NAMESPACE + ":blocks/metal_decoration2_lantern");
        ParticleTextureHandler.register(NAMESPACE, "metal_decoration2", "razor_wire", NAMESPACE + ":blocks/metal_decoration2_razor_wire");
        ParticleTextureHandler.register(NAMESPACE, "metal_decoration2", "steel_post", NAMESPACE + ":blocks/metal_decoration2_steel_post");
        ParticleTextureHandler.register(NAMESPACE, "metal_decoration2", "steel_slope", NAMESPACE + ":blocks/metal_decoration1_steel_scaffolding_noctm");
        ParticleTextureHandler.register(NAMESPACE, "metal_decoration2", "steel_wallmount", NAMESPACE + ":blocks/metal_decoration2_steel_wallmount");
        ParticleTextureHandler.register(NAMESPACE, "metal_decoration2", "toolbox", NAMESPACE + ":blocks/metal_decoration2_toolbox");
        ParticleTextureHandler.register(NAMESPACE, "metal_device1", "blast_furnace_preheater", NAMESPACE + ":blocks/metal_device1_blast_furnace_preheater");
        ParticleTextureHandler.register(NAMESPACE, "metal_device1", "tesla_coil", NAMESPACE + ":blocks/metal_device1_teslacoil");
        ParticleTextureHandler.register(NAMESPACE, "metal_device1", "turret_chem", NAMESPACE + ":blocks/metal_device1_chem_turret");
        ParticleTextureHandler.register(NAMESPACE, "metal_device1", "turret_gun", NAMESPACE + ":blocks/metal_device1_gun_turret");
        ParticleTextureHandler.register(NAMESPACE, "metal_device1_belljar", "belljar", NAMESPACE + ":blocks/metal_device1_glassbell");
        ParticleTextureHandler.register(NAMESPACE, "metal_device1_core_drill", "sample_drill", NAMESPACE + ":blocks/metal_device1_coresample_drill");
        ParticleTextureHandler.register(NAMESPACE, "metal_device1_floodlight", "floodlight", NAMESPACE + ":blocks/metal_device1_floodlight");
        ParticleTextureHandler.register(NAMESPACE, "metal_device1_lantern", "electric_lantern", NAMESPACE + ":blocks/metal_device1_electric_lantern");
        ParticleTextureHandler.register(NAMESPACE, "metal_device1_pipe", "fluid_pipe", NAMESPACE + ":blocks/metal_device1_fluid_pipe");
        ParticleTextureHandler.register(NAMESPACE, "metal_multiblock", "assembler", NAMESPACE + ":blocks/metal_multiblock_assembler");
        ParticleTextureHandler.register(NAMESPACE, "metal_multiblock", "lightningrod", NAMESPACE + ":blocks/metal_multiblock_lightningrod");
        ParticleTextureHandler.register(NAMESPACE, "metal_multiblock", "silo", NAMESPACE + ":blocks/metal_multiblock_silo");
        ParticleTextureHandler.register(NAMESPACE, "metal_multiblock", "tank", NAMESPACE + ":blocks/metal_multiblock_tank");
        ParticleTextureHandler.register(NAMESPACE, "metal_multiblock_bottling_machine", "bottling_machine", NAMESPACE + ":blocks/storage_steel");
        ParticleTextureHandler.register(NAMESPACE, "metal_multiblock_bucket_wheel", "bucket_wheel", NAMESPACE + ":blocks/metal_multiblock_bucket_wheel");
        ParticleTextureHandler.register(NAMESPACE, "stone_device", "blast_furnace_advanced", NAMESPACE + ":blocks/stone_device_blast_furnace_advanced");
        ParticleTextureHandler.register(NAMESPACE, "stone_device", "coresample", NAMESPACE + ":blocks/treated_wood_vertical");
        ParticleTextureHandler.register(NAMESPACE, "wooden_device0", "workbench", NAMESPACE + ":blocks/wooden_device_workbench");
        ParticleTextureHandler.register(NAMESPACE, "wooden_device0_workbench", "workbench", NAMESPACE + ":blocks/wooden_device_workbench");
        ParticleTextureHandler.register(NAMESPACE, "wooden_device1", "post", NAMESPACE + ":blocks/wooden_device_post");
        ParticleTextureHandler.register(NAMESPACE, "wooden_device1", "wallmount", NAMESPACE + ":blocks/wooden_device_wallmount");
        ParticleTextureHandler.register(NAMESPACE, "wooden_device1", "watermill", NAMESPACE + ":blocks/wooden_device_watermill");
        ParticleTextureHandler.register(NAMESPACE, "wooden_device1", "windmill", NAMESPACE + ":blocks/wooden_device_windmill");
        ParticleTextureHandler.register(NAMESPACE, "wooden_device1", "windmill_advanced", NAMESPACE + ":blocks/wooden_device_windmill_advanced");
    }
}
