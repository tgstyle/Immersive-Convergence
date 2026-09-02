package com.immersiveconvergence.api.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class BooleanButton extends StateButton<Boolean> {
    public BooleanButton(int x, int y, int w, int h, String name, Supplier<Boolean> state, ResourceLocation texture, int texU, int texV, int offsetDir, Consumer<BooleanButton> handler) { super(x, y, w, h, Component.literal(name), new Boolean[]{false, true}, () -> state.get() ? 1 : 0, texture, texU, texV, offsetDir, btn -> handler.accept((BooleanButton) btn)); }
}
