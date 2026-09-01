package com.immersiveconvergence.common;

import com.immersiveconvergence.ImmersiveConvergence;
import com.immersiveconvergence.api.block.BlockContext;
import com.immersiveconvergence.common.util.ICLogger;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IGuiTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class ICBlockContext {
    private static final BlockContext.GuiOpener NO_GUI = new BlockContext.GuiOpener() {
        @Override public <T extends TileEntity & IGuiTile> void open(EntityPlayer player, T tile) {}
    };

    public static final BlockContext CONTEXT = new BlockContext(ImmersiveConvergence.MODID, ImmersiveConvergence.creativeTab, ICContent.registeredICBlocks, ICContent.registeredICItems, ICLogger.logger, NO_GUI);
}
