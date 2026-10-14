package com.immersiveconvergence.core.compat.computer;

import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.util.compat.computers.cctweaked.CCLuaTypeConverter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class IEComputerValues {
    private IEComputerValues() {}

    public static Object item(ItemStack stack) { return CCLuaTypeConverter.INSTANCE.serialize(stack); }

    public static Object fluid(FluidStack stack) { return CCLuaTypeConverter.INSTANCE.serialize(stack); }

    public static Object[] recipe(ItemStack input, ItemStack itemOutput, FluidStack fluidOutput, int time) { return new Object[] {item(input), item(itemOutput), fluid(fluidOutput), time}; }

    @Nullable public static <R extends MultiblockRecipe> R firstRecipe(List<? extends MultiblockProcess<R, ?>> queue, Level level) { return queue.isEmpty() ? null : queue.get(0).getRecipe(level); }

    public static Map<String, Object> items(Map<String, ItemStack> stacks) {
        Map<String, Object> result = new LinkedHashMap<>();
        stacks.forEach((key, stack) -> result.put(key, item(stack)));
        return result;
    }
}
