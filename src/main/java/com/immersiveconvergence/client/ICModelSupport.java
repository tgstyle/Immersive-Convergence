package com.immersiveconvergence.client;

import com.immersiveconvergence.api.client.ICCustomStateMapper;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IMetaBlock;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

import java.util.Locale;

public final class ICModelSupport {
    private ICModelSupport() {}

    public static boolean isMetaBlock(Block block) { return block instanceof IMetaBlock; }

    public static void registerMetaBlock(Block block, ResourceLocation loc, Item blockItem) {
        IMetaBlock metaBlock = (IMetaBlock)block;
        if (metaBlock.useCustomStateMapper()) { ModelLoader.setCustomStateMapper(block, ICCustomStateMapper.getStateMapper(metaBlock)); }
        ModelLoader.setCustomMeshDefinition(blockItem, stack -> new ModelResourceLocation(loc, "inventory"));
        for (int meta = 0; meta < metaBlock.getMetaEnums().length; meta++) {
            String location = loc.toString();
            String properties = metaBlock.appendPropertiesToState() ? ("inventory," + metaBlock.getMetaProperty().getName() + "=" + metaBlock.getMetaEnums()[meta].toString().toLowerCase(Locale.US)) : "normal";
            if (metaBlock.useCustomStateMapper()) { location += "_" + metaBlock.getCustomStateMapping(meta, true); }
            ModelLoader.setCustomModelResourceLocation(blockItem, meta, new ModelResourceLocation(location, properties));
        }
    }
}
