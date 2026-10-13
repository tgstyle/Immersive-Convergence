package com.immersiveconvergence.api.util;

import blusunrize.immersiveengineering.api.client.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class ICTextUtils {
    public static MutableComponent applyFormat(Component component, ChatFormatting... color) { return TextUtils.applyFormat(component, color); }
}
