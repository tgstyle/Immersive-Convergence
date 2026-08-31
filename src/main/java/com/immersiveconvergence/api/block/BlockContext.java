package com.immersiveconvergence.api.block;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IGuiTile;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
public class BlockContext {
    public final String modid;
    public final CreativeTabs creativeTab;
    public final List<Block> registeredBlocks;
    public final List<Item> registeredItems;
    public final Logger logger;
    public final GuiOpener guiOpener;

    public BlockContext(String modid, CreativeTabs creativeTab, List<Block> registeredBlocks, List<Item> registeredItems, Logger logger, GuiOpener guiOpener) {
        this.modid = modid;
        this.creativeTab = creativeTab;
        this.registeredBlocks = registeredBlocks;
        this.registeredItems = registeredItems;
        this.logger = logger;
        this.guiOpener = guiOpener;
    }

    public interface GuiOpener {
        <T extends TileEntity & IGuiTile> void open(EntityPlayer player, T tile);
    }
}
