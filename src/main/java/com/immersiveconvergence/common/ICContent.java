package com.immersiveconvergence.common;

import com.immersiveconvergence.ImmersiveConvergence;
import com.immersiveconvergence.common.blocks.ICBlockDevice;
import com.immersiveconvergence.common.blocks.tileentities.TileEntityHeatCreative;
import com.immersiveconvergence.common.blocks.tileentities.TileEntityRotorCreative;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = ImmersiveConvergence.MODID)
public class ICContent {
    public static final List<Block> registeredICBlocks = new ArrayList<>();
    public static final List<Item> registeredICItems = new ArrayList<>();

    public static ICBlockDevice blockDevice;

    public static void preInit() {
        blockDevice = new ICBlockDevice();
        registerTile(TileEntityRotorCreative.class);
        registerTile(TileEntityHeatCreative.class);
    }

    private static void registerTile(Class<? extends TileEntity> tile) {
        String name = tile.getSimpleName().replaceFirst("TileEntity", "");
        GameRegistry.registerTileEntity(tile, new ResourceLocation(ImmersiveConvergence.MODID, name));
    }

    @SubscribeEvent public static void registerBlocks(RegistryEvent.Register<Block> event) {
        for (Block block : registeredICBlocks) { event.getRegistry().register(block.setRegistryName(createRegistryName(block.getTranslationKey()))); }
    }

    @SubscribeEvent public static void registerItems(RegistryEvent.Register<Item> event) {
        for (Item item : registeredICItems) { event.getRegistry().register(item.setRegistryName(createRegistryName(item.getTranslationKey()))); }
    }

    private static ResourceLocation createRegistryName(String unlocalized) {
        unlocalized = unlocalized.substring(unlocalized.indexOf("immersive"));
        unlocalized = unlocalized.replaceFirst("\\.", ":");
        return new ResourceLocation(unlocalized);
    }
}
