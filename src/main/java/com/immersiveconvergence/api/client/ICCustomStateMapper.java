package com.immersiveconvergence.api.client;

import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IMetaBlock;
import com.immersiveconvergence.common.util.ICLogger;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public class ICCustomStateMapper extends StateMapperBase {
    public static final Map<String, StateMapperBase> stateMappers = new HashMap<>();

    public static StateMapperBase getStateMapper(IMetaBlock metaBlock) {
        String key = metaBlock.getBlockName();
        StateMapperBase mapper = stateMappers.get(key);
        if (mapper == null) {
            mapper = metaBlock.getCustomMapper();
            if (mapper == null) { mapper = new ICCustomStateMapper(); }
            stateMappers.put(key, mapper);
        }
        return mapper;
    }

    @Override @Nonnull protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
        ResourceLocation registryName = Block.REGISTRY.getNameForObject(state.getBlock());
        try {
            IMetaBlock metaBlock = (IMetaBlock)state.getBlock();
            String custom = metaBlock.getCustomStateMapping(state.getBlock().getMetaFromState(state), false);
            ResourceLocation rl = custom != null ? new ResourceLocation(registryName + "_" + custom) : registryName;
            String properties = metaBlock.appendPropertiesToState() ? this.getPropertyString(state.getProperties()) : "normal";
            return new ModelResourceLocation(rl, properties);
        }
        catch (Exception e) {
            ICLogger.error("Failed to map the model location for " + registryName + ": " + e);
            return new ModelResourceLocation(registryName, this.getPropertyString(state.getProperties()));
        }
    }
}
