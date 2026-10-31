package com.immersiveconvergence.client;

import com.immersiveconvergence.api.bridge.IICClientBridge;
import com.immersiveconvergence.common.ie.IERenderBridge;

import net.minecraft.client.renderer.RenderType;

import javax.annotation.Nonnull;

public final class ICClientBridge implements IICClientBridge {
    @Override @Nonnull public RenderType translucentFullbright() { return IERenderBridge.translucentFullbright(); }
}
