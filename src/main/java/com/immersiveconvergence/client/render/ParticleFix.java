package com.immersiveconvergence.client.render;

import com.immersiveconvergence.ImmersiveConvergence;
import com.immersiveconvergence.api.client.split.SplitModelHandler;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = ImmersiveConvergence.MODID, value = Side.CLIENT)
public class ParticleFix {
    private static final String NAMESPACE = "immersiveengineering";
    private static final Map<String, Map<String, String>> SPRITES = new HashMap<>();

    static {
        fix("cloth_device", "balloon", "blocks/cloth_device_balloon");
        fix("connector", "connector_hv", "blocks/connector_connector_hv");
        fix("connector", "connector_lv", "blocks/connector_connector_lv");
        fix("connector", "connector_mv", "blocks/connector_connector_mv");
        fix("connector", "connector_probe", "blocks/connector_connector_probe");
        fix("connector", "connector_redstone", "blocks/connector_connector_redstone");
        fix("connector", "connector_structural", "blocks/connector_connector_structural");
        fix("connector", "relay_hv", "blocks/connector_relay_hv");
        fix("connector", "relay_lv", "blocks/connector_relay_lv");
        fix("connector", "relay_mv", "blocks/connector_relay_mv");
        fix("connector_breaker_switch", "breakerswitch", "blocks/connector_breaker_switch");
        fix("connector_energy_meter", "energy_meter", "blocks/connector_energy_meter");
        fix("connector_redstone_breaker", "redstone_breaker", "blocks/connector_redstone_breaker");
        fix("connector_transformer", "transformer", "blocks/connector_transformer");
        fix("connector_transformer_hv", "transformer_hv", "blocks/connector_transformer_hv");
        fix("metal_decoration2", "alu_slope", "blocks/metal_decoration1_aluminum_scaffolding_noctm");
        fix("metal_decoration2", "aluminum_post", "blocks/metal_decoration2_aluminum_post");
        fix("metal_decoration2", "aluminum_wallmount", "blocks/metal_decoration2_aluminum_wallmount");
        fix("metal_decoration2", "lantern", "blocks/metal_decoration2_lantern");
        fix("metal_decoration2", "razor_wire", "blocks/metal_decoration2_razor_wire");
        fix("metal_decoration2", "steel_post", "blocks/metal_decoration2_steel_post");
        fix("metal_decoration2", "steel_slope", "blocks/metal_decoration1_steel_scaffolding_noctm");
        fix("metal_decoration2", "steel_wallmount", "blocks/metal_decoration2_steel_wallmount");
        fix("metal_decoration2", "toolbox", "blocks/metal_decoration2_toolbox");
        fix("metal_device1", "blast_furnace_preheater", "blocks/metal_device1_blast_furnace_preheater");
        fix("metal_device1", "tesla_coil", "blocks/metal_device1_teslacoil");
        fix("metal_device1", "turret_chem", "blocks/metal_device1_chem_turret");
        fix("metal_device1", "turret_gun", "blocks/metal_device1_gun_turret");
        fix("metal_device1_belljar", "belljar", "blocks/metal_device1_glassbell");
        fix("metal_device1_core_drill", "sample_drill", "blocks/metal_device1_coresample_drill");
        fix("metal_device1_floodlight", "floodlight", "blocks/metal_device1_floodlight");
        fix("metal_device1_lantern", "electric_lantern", "blocks/metal_device1_electric_lantern");
        fix("metal_device1_pipe", "fluid_pipe", "blocks/metal_device1_fluid_pipe");
        fix("metal_multiblock", "assembler", "blocks/metal_multiblock_assembler");
        fix("metal_multiblock", "lightningrod", "blocks/metal_multiblock_lightningrod");
        fix("metal_multiblock", "silo", "blocks/metal_multiblock_silo");
        fix("metal_multiblock", "tank", "blocks/metal_multiblock_tank");
        fix("metal_multiblock_bottling_machine", "bottling_machine", "blocks/storage_steel");
        fix("metal_multiblock_bucket_wheel", "bucket_wheel", "blocks/metal_multiblock_bucket_wheel");
        fix("stone_device", "blast_furnace_advanced", "blocks/stone_device_blast_furnace_advanced");
        fix("stone_device", "coresample", "blocks/treated_wood_vertical");
        fix("wooden_device0", "workbench", "blocks/wooden_device_workbench");
        fix("wooden_device0_workbench", "workbench", "blocks/wooden_device_workbench");
        fix("wooden_device1", "post", "blocks/wooden_device_post");
        fix("wooden_device1", "wallmount", "blocks/wooden_device_wallmount");
        fix("wooden_device1", "watermill", "blocks/wooden_device_watermill");
        fix("wooden_device1", "windmill", "blocks/wooden_device_windmill");
        fix("wooden_device1", "windmill_advanced", "blocks/wooden_device_windmill_advanced");
    }

    private static void fix(String file, String type, String texture) { SPRITES.computeIfAbsent(file, key -> new HashMap<>()).put(type, NAMESPACE + ":" + texture); }

    @SubscribeEvent public static void onModelBake(ModelBakeEvent event) {
        IRegistry<ModelResourceLocation, IBakedModel> registry = event.getModelRegistry();
        TextureMap atlas = Minecraft.getMinecraft().getTextureMapBlocks();
        List<ModelResourceLocation> keys = new ArrayList<>();
        for (ModelResourceLocation mrl : registry.getKeys()) {
            if (!NAMESPACE.equals(mrl.getNamespace()) || mrl.getVariant().contains("inventory")) { continue; }
            Map<String, String> byType = SPRITES.get(mrl.getPath());
            if (byType != null && byType.containsKey(SplitModelHandler.value(mrl.getVariant(), "type"))) { keys.add(mrl); }
        }
        for (ModelResourceLocation mrl : keys) {
            String sprite = SPRITES.get(mrl.getPath()).get(SplitModelHandler.value(mrl.getVariant(), "type"));
            registry.putObject(mrl, new ParticleModel(registry.getObject(mrl), atlas.getAtlasSprite(sprite)));
        }
    }

    private static final class ParticleModel implements IBakedModel {
        private final IBakedModel base;
        private final TextureAtlasSprite particle;

        ParticleModel(IBakedModel base, TextureAtlasSprite particle) {
            this.base = base;
            this.particle = particle;
        }

        @Override @Nonnull public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) { return base.getQuads(state, side, rand); }

        @Override public boolean isAmbientOcclusion() { return base.isAmbientOcclusion(); }

        @Override public boolean isGui3d() { return base.isGui3d(); }

        @Override public boolean isBuiltInRenderer() { return base.isBuiltInRenderer(); }

        @Override @Nonnull public TextureAtlasSprite getParticleTexture() { return particle; }

        @SuppressWarnings("deprecation") @Override @Nonnull public ItemCameraTransforms getItemCameraTransforms() { return base.getItemCameraTransforms(); }

        @Override @Nonnull public ItemOverrideList getOverrides() { return base.getOverrides(); }
    }
}
