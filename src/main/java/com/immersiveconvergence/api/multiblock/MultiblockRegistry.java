package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.common.multiblock.IEMultiblockBridge;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class MultiblockRegistry {
    private static final List<ICMultiblock> MULTIBLOCKS = new ArrayList<>();

    public static boolean immersiveEngineering() { return Loader.isModLoaded("immersiveengineering"); }

    public static <T extends ICMultiblock> T register(T multiblock) {
        MULTIBLOCKS.add(multiblock);
        if (immersiveEngineering()) { IEMultiblockBridge.register(multiblock); }
        return multiblock;
    }

    public static List<ICMultiblock> getMultiblocks() { return Collections.unmodifiableList(MULTIBLOCKS); }

    public static boolean exists(String uniqueName) {
        if (immersiveEngineering()) { return IEMultiblockBridge.exists(uniqueName); }
        for (ICMultiblock multiblock : MULTIBLOCKS) {
            if (multiblock.getUniqueName().equalsIgnoreCase(uniqueName)) { return true; }
        }
        return false;
    }

    public static boolean formFirstMatching(World world, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack, Predicate<String> allowed) {
        if (immersiveEngineering()) { return IEMultiblockBridge.formFirstMatching(world, pos, side, player, stack, allowed); }
        for (ICMultiblock multiblock : MULTIBLOCKS) {
            if (!multiblock.isBlockTrigger(world.getBlockState(pos))) { continue; }
            if (!allowed.test(multiblock.getUniqueName())) { continue; }
            if (multiblock.createStructure(world, pos, side, player)) { return true; }
        }
        return false;
    }

    public static boolean formationCancelled(EntityPlayer player, ICMultiblock multiblock, BlockPos pos, ItemStack hammer) {
        return immersiveEngineering() && IEMultiblockBridge.formationCancelled(player, multiblock, pos, hammer);
    }

    public static void formationDone(EntityPlayer player, ICMultiblock multiblock, BlockPos pos, ItemStack hammer) {
        if (immersiveEngineering()) { IEMultiblockBridge.formationDone(player, multiblock, pos, hammer); }
    }
}
