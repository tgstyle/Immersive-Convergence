package com.immersiveconvergence.api.client;

import blusunrize.immersiveengineering.api.energy.wires.WireApi;
import blusunrize.immersiveengineering.client.ClientProxy;
import blusunrize.immersiveengineering.client.IECustomStateMapper;
import blusunrize.immersiveengineering.client.models.ModelConveyor;
import blusunrize.immersiveengineering.client.models.obj.IEOBJLoader;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IIEMetaBlock;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import java.lang.reflect.Field;
import java.util.Map;

@SuppressWarnings("unused")
public class ICModels {
    public static void registerOBJLoader() { ModelLoaderRegistry.registerLoader(IEOBJLoader.instance); }

    public static void addOBJDomain(String modid) { IEOBJLoader.instance.addDomain(modid); }

    public static boolean isMetaBlock(Block block) { return block instanceof IIEMetaBlock; }

    public static boolean usesCustomStateMapper(Block block) { return block instanceof IIEMetaBlock && ((IIEMetaBlock)block).useCustomStateMapper(); }

    public static StateMapperBase customStateMapper(Block block) { return IECustomStateMapper.getStateMapper((IIEMetaBlock)block); }

    public static FontRenderer itemFont() { return ClientProxy.itemFont; }

    @SuppressWarnings("deprecation")
    public static void registerConnectorForRender(String key, ResourceLocation baseModel) { WireApi.registerConnectorForRender(key, baseModel, null); }

    public static void clearConveyorModelCaches() throws ReflectiveOperationException {
        ModelConveyor.modelCache.clear();
        Field itemCacheField = ModelConveyor.class.getDeclaredField("itemModelCache");
        itemCacheField.setAccessible(true);
        ((Map<?, ?>)itemCacheField.get(null)).clear();
    }
}
