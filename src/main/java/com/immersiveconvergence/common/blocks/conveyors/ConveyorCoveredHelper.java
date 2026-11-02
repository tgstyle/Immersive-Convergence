package com.immersiveconvergence.common.blocks.conveyors;

import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.blocks.metal.BlockTypes_MetalDecoration1;
import blusunrize.immersiveengineering.common.blocks.wooden.BlockTypes_WoodenDecoration;
import blusunrize.immersiveengineering.common.util.Utils;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraftforge.oredict.OreDictionary;

public class ConveyorCoveredHelper {
    public static final ArrayList<com.google.common.base.Function<ItemStack, Boolean>> validConveyorCovers = new ArrayList<>();
    public static final ItemStack defaultCover;

    static {
        final List<ItemStack> scaffolds = Lists.newArrayList(
                new ItemStack(IEContent.blockWoodenDecoration, 1, BlockTypes_WoodenDecoration.SCAFFOLDING.getMeta()),
                new ItemStack(IEContent.blockMetalDecoration1, 1, BlockTypes_MetalDecoration1.STEEL_SCAFFOLDING_1.getMeta()),
                new ItemStack(IEContent.blockMetalDecoration1, 1, BlockTypes_MetalDecoration1.STEEL_SCAFFOLDING_2.getMeta()),
                new ItemStack(IEContent.blockMetalDecoration1, 1, BlockTypes_MetalDecoration1.ALUMINUM_SCAFFOLDING_0.getMeta()),
                new ItemStack(IEContent.blockMetalDecoration1, 1, BlockTypes_MetalDecoration1.ALUMINUM_SCAFFOLDING_1.getMeta()),
                new ItemStack(IEContent.blockMetalDecoration1, 1, BlockTypes_MetalDecoration1.ALUMINUM_SCAFFOLDING_2.getMeta())
        );
        validConveyorCovers.add(input -> {
            if (input == null) { return false; }
            for (ItemStack stack : scaffolds) { if (OreDictionary.itemMatches(stack, input, false)) { return true; } }
            return false;
        });
        validConveyorCovers.add(input -> input != null && Utils.compareToOreName(input, "blockGlass"));

        defaultCover = new ItemStack(IEContent.blockMetalDecoration1, 1, BlockTypes_MetalDecoration1.STEEL_SCAFFOLDING_0.getMeta());
    }

    public static boolean handleCoverInteraction(TileEntity tile, EntityPlayer player, EnumHand hand, ItemStack heldItem, Supplier<ItemStack> coverGet, Consumer<ItemStack> coverSet) {
        ItemStack cover = coverGet.get();
        if (heldItem.isEmpty() && player.isSneaking() && !cover.isEmpty()) {
            if (!tile.getWorld().isRemote && tile.getWorld().getGameRules().getBoolean("doTileDrops")) {
                EntityItem entityitem = player.dropItem(cover.copy(), false);
                if (entityitem != null) { entityitem.setNoPickupDelay(); }
            }
            coverSet.accept(ItemStack.EMPTY);
            return true;
        }
        if (!heldItem.isEmpty() && !player.isSneaking()) {
            for (com.google.common.base.Function<ItemStack, Boolean> func : validConveyorCovers) {
                if (func.apply(heldItem) == Boolean.TRUE && !OreDictionary.itemMatches(cover, heldItem, true)) {
                    if (!tile.getWorld().isRemote && !cover.isEmpty() && tile.getWorld().getGameRules().getBoolean("doTileDrops")) {
                        EntityItem entityitem = player.dropItem(cover.copy(), false);
                        if (entityitem != null) { entityitem.setNoPickupDelay(); }
                    }
                    coverSet.accept(Utils.copyStackWithAmount(heldItem, 1));
                    heldItem.shrink(1);
                    if (heldItem.getCount() <= 0) { player.setHeldItem(hand, ItemStack.EMPTY); }
                    return true;
                }
            }
        }
        return false;
    }
}
