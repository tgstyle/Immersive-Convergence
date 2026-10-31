package com.immersiveconvergence.common.bridge;

import com.immersiveconvergence.api.bridge.IICBridgeService;
import com.immersiveconvergence.api.bridge.IICIntegrationBridge;
import com.immersiveconvergence.api.bridge.IICMultiblockBridge;

import javax.annotation.Nonnull;

public final class ICBridgeService implements IICBridgeService {
    private final IICMultiblockBridge multiblock = new ICMultiblockBridge();
    private final IICIntegrationBridge integration = new ICIntegrationBridge();

    @Override @Nonnull public IICMultiblockBridge multiblock() { return multiblock; }

    @Override @Nonnull public IICIntegrationBridge integration() { return integration; }
}
