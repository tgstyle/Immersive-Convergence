package com.immersiveconvergence.client;

import com.immersiveconvergence.ImmersiveConvergence;
import com.immersiveconvergence.api.client.split.SplitModelHandler;
import com.immersiveconvergence.api.network.BinaryTileSyncMessage;
import com.immersiveconvergence.api.network.MessageStopSound;
import com.immersiveconvergence.api.network.TileSyncMessage;
import com.immersiveconvergence.common.CommonProxy;
import com.immersiveconvergence.common.ICContent;
import com.immersiveconvergence.common.multiblock.IEMultiblockRegistry;
import com.immersiveconvergence.common.blocks.tileentities.TileEntityRotorCreative;
import com.immersiveconvergence.client.event.ICClientEventHandler;
import com.immersiveconvergence.client.render.TileRenderRotorCreative;
import com.immersiveconvergence.client.render.ip.IPPumpjackSupport;

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
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
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
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new ICClientEventHandler());
        registerSplitModels();
        if (Loader.isModLoaded("immersivepetroleum")) { IPPumpjackSupport.init(); }
    }

    @Override public void loadComplete() { if (Loader.isModLoaded("immersivepetroleum")) { IPPumpjackSupport.bindRenderer(); } }

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void registerPetroleumModels(ModelRegistryEvent event) { if (Loader.isModLoaded("immersivepetroleum")) { IPPumpjackSupport.registerStateMapper(); } }

    private static void registerSplitModels() {
        String ie = IEMultiblockRegistry.MODID;
        String[][] ownFiles = {{"arc_furnace", "IE:ArcFurnace"}, {"auto_workbench", "IE:AutoWorkbench"}, {"bottling_machine", "IE:BottlingMachine"}, {"crusher", "IE:Crusher"}, {"diesel_generator", "IE:DieselGenerator"}, {"excavator", "IE:Excavator"}, {"fermenter", "IE:Fermenter"}, {"metal_press", "IE:MetalPress"}, {"mixer", "IE:Mixer"}, {"refinery", "IE:Refinery"}, {"squeezer", "IE:Squeezer"}};
        for (String[] machine : ownFiles) {
            String file = "metal_multiblock_" + machine[0];
            SplitModelHandler.register(ie, file, null, file, null, false, () -> IEMultiblockRegistry.get(machine[1]));
        }
        String[][] sharedFile = {{"tank", "IE:SheetmetalTank"}, {"silo", "IE:Silo"}, {"assembler", "IE:Assembler"}, {"lightningrod", "IE:Lightningrod"}};
        for (String[] machine : sharedFile) { SplitModelHandler.register(ie, "metal_multiblock", machine[0], "metal_multiblock", machine[0], false, () -> IEMultiblockRegistry.get(machine[1])); }
        if (Loader.isModLoaded("immersivepetroleum")) { SplitModelHandler.register("immersivepetroleum", "metal_multiblock_distillationtowerparent", null, "metal_multiblock", "distillation_tower", true, () -> IEMultiblockRegistry.get("IP:DistillationTower")); }
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
