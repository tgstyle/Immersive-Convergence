package com.immersiveconvergence.client.render.ip;

import com.immersiveconvergence.api.client.split.SplitModelHandler;
import com.immersiveconvergence.common.multiblock.IEMultiblockRegistry;
import com.immersiveconvergence.common.util.ICLogger;

import com.google.common.collect.ImmutableMap;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.blocks.metal.TileEntityPumpjack;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class IPPumpjackSupport {
    private static final String NAMESPACE = "immersivepetroleum";
    public static IBakedModel body, arm, swing, connector, wellLong, wellShort;

    private IPPumpjackSupport() {}

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new IPPumpjackSupport());
        SplitModelHandler.register(NAMESPACE, "metal_multiblock_pumpjackparent", null, "metal_multiblock", "pumpjack", false, false, () -> IEMultiblockRegistry.get("IP:Pumpjack"));
    }

    public static void registerStateMapper() { ModelLoader.setCustomStateMapper(IPContent.blockMetalMultiblock, new IPPumpjackStateMapper()); }

    public static void bindRenderer() { ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPumpjack.TileEntityPumpjackParent.class, new TileRenderIPPumpjack()); }

    @SubscribeEvent public void onTextureStitch(TextureStitchEvent.Pre event) { event.getMap().setTextureEntry(new PumpjackSprite()); }

    @SubscribeEvent public void onModelBake(ModelBakeEvent event) {
        body = bake("pumpjack");
        arm = bake("pumpjack_arm");
        swing = bake("pumpjack_swing");
        connector = bake("pumpjack_connector");
        wellLong = bake("pumpjack_well_long");
        wellShort = bake("pumpjack_well_short");
    }

    private static IBakedModel bake(String name) {
        try {
            IModel model = ModelLoaderRegistry.getModel(new ResourceLocation(NAMESPACE, "block/multiblock/" + name + ".obj"));
            return model.process(ImmutableMap.of("flip-v", "true")).bake(TRSRTransformation.identity(), DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter());
        }
        catch (Exception e) {
            ICLogger.error("Could not bake pumpjack model " + name + ": " + e);
            return null;
        }
    }
}
