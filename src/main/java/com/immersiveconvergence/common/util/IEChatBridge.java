package com.immersiveconvergence.common.util;

import blusunrize.immersiveengineering.common.util.ChatUtils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;

public final class IEChatBridge {
    private IEChatBridge() {}

    public static void sendServerNoSpamMessages(EntityPlayer player, ITextComponent... messages) { ChatUtils.sendServerNoSpamMessages(player, messages); }
}
