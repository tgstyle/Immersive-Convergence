package com.immersiveconvergence.core.compat.computer;

import com.immersiveconvergence.api.util.ComputerValues;

import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInWorld;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class IEComputerValues {
    private IEComputerValues() {}

    public static Object item(ItemStack stack) { return ComputerValues.item(stack); }

    public static Object fluid(FluidStack stack) { return ComputerValues.fluid(stack); }

    public static Object[] recipe(ItemStack input, ItemStack itemOutput, FluidStack fluidOutput, int time) { return new Object[] {item(input), item(itemOutput), fluid(fluidOutput), time}; }

    @Nullable public static <R extends MultiblockRecipe> R firstRecipe(List<? extends MultiblockProcess<R, ?>> queue, Level level) { return queue.isEmpty() ? null : queue.getFirst().getRecipe(level); }

    public static <R extends MultiblockRecipe> Map<Integer, Object> inputQueue(List<? extends MultiblockProcess<R, ?>> queue, Level level, Function<R, ItemStack> output) {
        Map<Integer, Object> result = new LinkedHashMap<>();
        for (int i = 0; i < queue.size(); i++) {
            MultiblockProcess<R, ?> process = queue.get(i);
            if (!(process instanceof MultiblockProcessInWorld<?> inWorld)) { continue; }
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("progress", process.processTick);
            entry.put("maxProgress", process.getMaxTicks(level));
            List<Object> input = new ArrayList<>(inWorld.inputItems.size());
            for (ItemStack stack : inWorld.inputItems) { input.add(item(stack)); }
            entry.put("input", input);
            entry.put("output", item(output.apply(process.getRecipe(level))));
            result.put(i + 1, entry);
        }
        return result;
    }

    public static Map<String, Object> items(Map<String, ItemStack> stacks) {
        Map<String, Object> result = new LinkedHashMap<>();
        stacks.forEach((key, stack) -> result.put(key, item(stack)));
        return result;
    }
}
