package com.immersiveconvergence.api.client;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IMetaBlock;
import com.immersiveconvergence.common.client.IEClientBridge;

import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class ICModels {
    public static void registerOBJLoader() {
        if (ICMods.immersiveEngineering()) { IEClientBridge.registerOBJLoader(); }
    }

    public static void addOBJDomain(String modid) {
        if (ICMods.immersiveEngineering()) { IEClientBridge.addOBJDomain(modid); }
    }

    public static boolean isMetaBlock(Block block) { return block instanceof IMetaBlock; }

    public static boolean usesCustomStateMapper(Block block) { return block instanceof IMetaBlock && ((IMetaBlock)block).useCustomStateMapper(); }

    public static StateMapperBase customStateMapper(Block block) { return ICCustomStateMapper.getStateMapper((IMetaBlock)block); }

    @Nullable public static FontRenderer itemFont() { return ICMods.immersiveEngineering() ? IEClientBridge.itemFont() : null; }

    public static void registerConnectorForRender(String key, ResourceLocation baseModel) {
        if (ICMods.immersiveEngineering()) { IEClientBridge.registerConnectorForRender(key, baseModel); }
    }

    public static void clearConveyorModelCaches() throws ReflectiveOperationException {
        if (ICMods.immersiveEngineering()) { IEClientBridge.clearConveyorModelCaches(); }
    }
}
