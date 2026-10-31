package com.immersiveconvergence.common.bridge;

import com.immersiveconvergence.api.bridge.IICBridgeService;
import com.immersiveconvergence.api.bridge.IICIntegrationBridge;
import com.immersiveconvergence.api.bridge.IICMultiblockBridge;
import com.immersiveconvergence.api.bridge.IICRdplBridge;
import com.immersiveconvergence.api.bridge.IICTileBridge;

import javax.annotation.Nonnull;

public final class ICBridgeService implements IICBridgeService {
    private final IICTileBridge tile = new ICTileBridge();
    private final IICMultiblockBridge multiblock = new ICMultiblockBridge();
    private final IICIntegrationBridge integration = new ICIntegrationBridge();
    private final IICRdplBridge rdpl = new ICRdplBridge();

    @Override @Nonnull public IICTileBridge tile() { return tile; }

    @Override @Nonnull public IICMultiblockBridge multiblock() { return multiblock; }

    @Override @Nonnull public IICIntegrationBridge integration() { return integration; }

    @Override @Nonnull public IICRdplBridge rdpl() { return rdpl; }
}
