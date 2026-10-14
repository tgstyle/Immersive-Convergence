package com.immersiveconvergence.common.ie;

import blusunrize.immersiveengineering.api.IEApi;
import net.minecraft.world.item.ItemStack;

public final class IERecipeBridge {
    private IERecipeBridge() {}

    public static ItemStack preferredStack(ItemStack[] stacks) { return IEApi.getPreferredStackbyMod(stacks); }
}
