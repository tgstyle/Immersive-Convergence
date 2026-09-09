package com.immersiveconvergence.api.multiblock;

import blusunrize.immersiveengineering.api.MultiblockHandler;
import blusunrize.immersiveengineering.common.util.advancements.IEAdvancements;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Predicate;

@SuppressWarnings("unused")
public class MultiblockRegistry {
    public static <T extends MultiblockHandler.IMultiblock> T register(T multiblock) {
        MultiblockHandler.registerMultiblock(multiblock);
        return multiblock;
    }

    public static boolean exists(String uniqueName) {
        for (MultiblockHandler.IMultiblock multiblock : MultiblockHandler.getMultiblocks()) {
            if (multiblock.getUniqueName().equalsIgnoreCase(uniqueName)) { return true; }
        }
        return false;
    }

    public static boolean formFirstMatching(World world, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack, Predicate<String> allowed) {
        for (MultiblockHandler.IMultiblock multiblock : MultiblockHandler.getMultiblocks()) {
            if (!multiblock.isBlockTrigger(world.getBlockState(pos))) { continue; }
            if (!allowed.test(multiblock.getUniqueName())) { continue; }
            if (fireFormationPre(player, multiblock, pos, stack)) { continue; }
            if (multiblock.createStructure(world, pos, side, player)) {
                if (player instanceof EntityPlayerMP) { IEAdvancements.TRIGGER_MULTIBLOCK.trigger((EntityPlayerMP)player, multiblock, stack); }
                return true;
            }
        }
        return false;
    }

    public static boolean formationCancelled(EntityPlayer player, TemplateMultiblock multiblock, BlockPos pos, ItemStack hammer) { return fireFormationPre(player, multiblock, pos, hammer); }

    public static void formationDone(EntityPlayer player, TemplateMultiblock multiblock, BlockPos pos, ItemStack hammer) { MultiblockHandler.fireMultiblockFormationEventPost(player, multiblock, pos, hammer); }

    private static boolean fireFormationPre(EntityPlayer player, MultiblockHandler.IMultiblock multiblock, BlockPos pos, ItemStack hammer) { return MultiblockHandler.fireMultiblockFormationEventPre(player, multiblock, pos, hammer).isCanceled(); }
}
