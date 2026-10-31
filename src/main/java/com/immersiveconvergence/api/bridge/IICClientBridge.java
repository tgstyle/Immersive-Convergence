package com.immersiveconvergence.api.bridge;

import net.minecraft.client.renderer.RenderType;

import javax.annotation.Nonnull;

public interface IICClientBridge {
    @Nonnull RenderType translucentFullbright();
}
