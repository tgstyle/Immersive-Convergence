package com.immersiveconvergence.api.client.gui;

import net.minecraft.network.chat.Component;

import java.util.List;

@SuppressWarnings({"unused", "RedundantSuppression"}) public interface IGuiTooltipWidget {
    void gatherTooltip(int mouseX, int mouseY, List<Component> tooltip);
}
