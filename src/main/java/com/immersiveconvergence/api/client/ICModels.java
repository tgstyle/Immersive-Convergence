package com.immersiveconvergence.api.client;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IMetaBlock;
import com.immersiveconvergence.client.ICClientWires;
import com.immersiveconvergence.client.models.ICConnectionLoader;
import com.immersiveconvergence.client.models.ICObjIeLoader;
import com.immersiveconvergence.api.bridge.ICBridges;

import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import javax.annotation.Nullable;
import java.util.Set;

@SuppressWarnings("unused")
public class ICModels {
    public static void registerOBJLoader() {
        if (ICMods.immersiveEngineering()) { ICBridges.client().registerOBJLoader(); }
        else { ModelLoaderRegistry.registerLoader(ICObjIeLoader.INSTANCE); }
    }

    public static void addOBJDomain(String modid) {
        if (ICMods.immersiveEngineering()) { ICBridges.client().addOBJDomain(modid); }
        else { ICObjIeLoader.addDomain(modid); }
    }

    public static boolean isMetaBlock(Block block) { return block instanceof IMetaBlock; }

    public static boolean usesCustomStateMapper(Block block) { return block instanceof IMetaBlock && ((IMetaBlock)block).useCustomStateMapper(); }

    public static StateMapperBase customStateMapper(Block block) { return ICCustomStateMapper.getStateMapper((IMetaBlock)block); }

    @Nullable public static FontRenderer itemFont() { return ICMods.immersiveEngineering() ? ICBridges.client().itemFont() : null; }

    public static void registerConnectorForRender(String key, ResourceLocation baseModel) {
        if (ICMods.immersiveEngineering()) { ICBridges.client().registerConnectorForRender(key, baseModel); }
        else { ICConnectionLoader.register(key, baseModel); }
    }

    public static Set<?> nativeConnections(BlockPos pos) { return ICClientWires.getEnds(pos); }

    public static void clearConveyorModelCaches() throws ReflectiveOperationException {
        if (ICMods.immersiveEngineering()) { ICBridges.client().clearConveyorModelCaches(); }
    }
}
