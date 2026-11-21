package com.immersiveconvergence.api.util;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.bridge.ICBridges;
import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class ICUtils {
    public static final Random RANDOM = new Random();
    public static final RandomSource RANDOM_SOURCE = RandomSource.createNewThreadLocalInstance();
    private static final TagKey<Item> FORMATION_TOOLS = TagKey.create(Registries.ITEM, ICLib.rl("tools/formation"));
    private static final TagKey<Item> TOGGLE_TOOLS = TagKey.create(Registries.ITEM, ICLib.rl("tools/toggle"));
    private static final TagKey<Item> SCREWDRIVERS = TagKey.create(Registries.ITEM, ICLib.rl("tools/screwdrivers"));

    public static boolean isHammer(ItemStack stack) { return stack.is(FORMATION_TOOLS); }

    public static boolean isScrewdriver(ItemStack stack) { return stack.is(SCREWDRIVERS); }

    public static boolean isToggleTool(ItemStack stack) { return stack.is(TOGGLE_TOOLS); }

    public static void addFutureServerTask(Level level, Runnable task) {
        MinecraftServer server = level.getServer();
        if (server != null) { server.tell(new TickTask(server.getTickCount(), task)); }
    }

    public static void updateObservers(Level level, BlockPos pos) { level.getBlockState(pos).updateNeighbourShapes(level, pos, Block.UPDATE_ALL); }

    public static ItemStack preferredStack(ItemStack[] stacks) {
        if (ICMods.immersiveEngineering()) { return ICBridges.integration().preferredStack(stacks); }
        return Arrays.stream(stacks).min(Comparator.comparing(stack -> BuiltInRegistries.ITEM.getKey(stack.getItem()))).orElseThrow(() -> new IllegalArgumentException("Empty array?"));
    }
}
