package com.immersiveconvergence.client;

import com.immersiveconvergence.ImmersiveConvergence;
import com.immersiveconvergence.api.network.BinaryTileSyncMessage;
import com.immersiveconvergence.api.network.MessageStopSound;
import com.immersiveconvergence.api.network.TileSyncMessage;
import com.immersiveconvergence.common.CommonProxy;
import com.immersiveconvergence.common.ICContent;
import com.immersiveconvergence.common.blocks.tileentities.TileEntityRotorCreative;
import com.immersiveconvergence.client.render.TileRenderRotorCreative;

import blusunrize.immersiveengineering.client.IECustomStateMapper;
import blusunrize.immersiveengineering.client.models.obj.IEOBJLoader;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IIEMetaBlock;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Locale;

import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = ImmersiveConvergence.MODID, value = Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override public void preInit() {
        super.preInit();
        OBJLoader.INSTANCE.addDomain(ImmersiveConvergence.MODID);
        IEOBJLoader.instance.addDomain(ImmersiveConvergence.MODID);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRotorCreative.class, new TileRenderRotorCreative());
    }

    @SubscribeEvent public static void registerModels(ModelRegistryEvent event) {
        for (Block block : ICContent.registeredICBlocks) {
            ResourceLocation loc = Block.REGISTRY.getNameForObject(block);
            Item blockItem = Item.getItemFromBlock(block);
            if (!(block instanceof IIEMetaBlock)) {
                ModelLoader.setCustomModelResourceLocation(blockItem, 0, new ModelResourceLocation(loc, "inventory"));
                continue;
            }
            IIEMetaBlock metaBlock = (IIEMetaBlock)block;
            if (metaBlock.useCustomStateMapper()) { ModelLoader.setCustomStateMapper(block, IECustomStateMapper.getStateMapper(metaBlock)); }
            ModelLoader.setCustomMeshDefinition(blockItem, stack -> new ModelResourceLocation(loc, "inventory"));
            for (int meta = 0; meta < metaBlock.getMetaEnums().length; meta++) {
                String location = loc.toString();
                String properties = metaBlock.appendPropertiesToState() ? ("inventory," + metaBlock.getMetaProperty().getName() + "=" + metaBlock.getMetaEnums()[meta].toString().toLowerCase(Locale.US)) : null;
                if (metaBlock.useCustomStateMapper()) { location += "_" + metaBlock.getCustomStateMapping(meta, true); }
                ModelLoader.setCustomModelResourceLocation(blockItem, meta, new ModelResourceLocation(location, properties));
            }
        }
    }

    @Override public void init() {
        ImmersiveConvergence.packetHandler.registerMessage(TileSyncMessage.HandlerClient.class, TileSyncMessage.class, 0, Side.CLIENT);
        ImmersiveConvergence.packetHandler.registerMessage(TileSyncMessage.HandlerServer.class, TileSyncMessage.class, 0, Side.SERVER);
        ImmersiveConvergence.packetHandler.registerMessage(MessageStopSound.HandlerClient.class, MessageStopSound.class, 1, Side.CLIENT);
        ImmersiveConvergence.packetHandler.registerMessage(BinaryTileSyncMessage.HandlerClient.class, BinaryTileSyncMessage.class, 3, Side.CLIENT);
        ImmersiveConvergence.packetHandler.registerMessage(BinaryTileSyncMessage.HandlerServer.class, BinaryTileSyncMessage.class, 3, Side.SERVER);
    }

    @Override public void postInit() { super.postInit(); }
}
