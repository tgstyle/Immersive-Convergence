package com.immersiveconvergence.api.util;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.utils.SafeChunkUtils;
import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

import java.util.Random;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class ICUtils {
    public static final Random RANDOM = ApiUtils.RANDOM;
    public static final RandomSource RANDOM_SOURCE = ApiUtils.RANDOM_SOURCE;

    public static boolean isHammer(ItemStack stack) { return Utils.isHammer(stack); }

    public static boolean isChunkSafe(LevelAccessor w, BlockPos pos) { return SafeChunkUtils.isChunkSafe(w, pos); }
}
