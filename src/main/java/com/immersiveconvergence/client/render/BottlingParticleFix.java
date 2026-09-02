package com.immersiveconvergence.client.render;

import com.immersiveconvergence.ImmersiveConvergence;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = ImmersiveConvergence.MODID, value = Side.CLIENT)
public class BottlingParticleFix {
    private static final String NAMESPACE = "immersiveengineering";
    private static final String PATH = "metal_multiblock_bottling_machine";
    private static final String PARTICLE = "immersiveengineering:blocks/storage_steel";

    @SubscribeEvent public static void onModelBake(ModelBakeEvent event) {
        IRegistry<ModelResourceLocation, IBakedModel> registry = event.getModelRegistry();
        TextureAtlasSprite particle = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(PARTICLE);
        List<ModelResourceLocation> keys = new ArrayList<>();
        for (ModelResourceLocation mrl : registry.getKeys()) {
            if (NAMESPACE.equals(mrl.getNamespace()) && PATH.equals(mrl.getPath()) && !mrl.getVariant().contains("inventory")) { keys.add(mrl); }
        }
        for (ModelResourceLocation mrl : keys) { registry.putObject(mrl, new ParticleModel(registry.getObject(mrl), particle)); }
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
