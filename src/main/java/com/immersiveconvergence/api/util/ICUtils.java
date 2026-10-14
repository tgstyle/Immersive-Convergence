package com.immersiveconvergence.api.util;

import com.immersiveconvergence.api.multiblock.util.CapabilityReference;
import com.immersiveconvergence.api.multiblock.util.DirectionalBlockPos;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.utils.SafeChunkUtils;
import blusunrize.immersiveengineering.common.util.Utils;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.Random;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class ICUtils {
    public static final Random RANDOM = ApiUtils.RANDOM;
    public static final RandomSource RANDOM_SOURCE = ApiUtils.RANDOM_SOURCE;

    public static FluidStack jsonDeserializeFluidStack(JsonObject jsonObject) { return ApiUtils.jsonDeserializeFluidStack(jsonObject); }

    public static boolean isHammer(ItemStack stack) { return Utils.isHammer(stack); }

    public static boolean isChunkSafe(LevelAccessor w, BlockPos pos) { return SafeChunkUtils.isChunkSafe(w, pos); }

    public static <T> CapabilityReference<T> forNeighbor(BlockEntity local, Capability<T> cap, @Nonnull Direction side) { return CapabilityReference.forBlockEntityAt(local, () -> new DirectionalBlockPos(local.getBlockPos().relative(side), side.getOpposite()), cap); }
}
