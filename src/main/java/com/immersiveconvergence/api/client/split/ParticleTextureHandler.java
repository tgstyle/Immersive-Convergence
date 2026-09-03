package com.immersiveconvergence.api.client.split;

import com.immersiveconvergence.ImmersiveConvergence;

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

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = ImmersiveConvergence.MODID, value = Side.CLIENT)
public class ParticleTextureHandler {
    private static final Map<String, Map<String, Map<String, String>>> SPRITES = new HashMap<>();

    public static void register(String namespace, String stateFile, String type, String texture) { SPRITES.computeIfAbsent(namespace, key -> new HashMap<>()).computeIfAbsent(stateFile, key -> new HashMap<>()).put(type, texture); }

    @SubscribeEvent public static void onModelBake(ModelBakeEvent event) {
        IRegistry<ModelResourceLocation, IBakedModel> registry = event.getModelRegistry();
        TextureMap atlas = Minecraft.getMinecraft().getTextureMapBlocks();
        List<ModelResourceLocation> keys = new ArrayList<>();
        for (ModelResourceLocation mrl : registry.getKeys()) {
            if (mrl.getVariant().contains("inventory") || spriteFor(mrl) == null) { continue; }
            keys.add(mrl);
        }
        for (ModelResourceLocation mrl : keys) { registry.putObject(mrl, new ParticleModel(registry.getObject(mrl), atlas.getAtlasSprite(spriteFor(mrl)))); }
    }

    @Nullable private static String spriteFor(ModelResourceLocation mrl) {
        Map<String, Map<String, String>> byFile = SPRITES.get(mrl.getNamespace());
        Map<String, String> byType = byFile == null ? null : byFile.get(mrl.getPath());
        return byType == null ? null : byType.get(SplitModelHandler.variantValue(mrl.getVariant(), "type"));
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
