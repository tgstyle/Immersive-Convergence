package com.immersiveconvergence.api.util;

import java.util.stream.Stream;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings({"unused", "RedundantSuppression"}) public interface IItemDropProvider { Stream<ItemStack> getDroppedItems(); }
