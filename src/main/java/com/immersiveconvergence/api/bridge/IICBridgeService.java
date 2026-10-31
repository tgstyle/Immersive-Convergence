package com.immersiveconvergence.api.bridge;

import javax.annotation.Nonnull;

public interface IICBridgeService {
    @Nonnull IICTileBridge tile();

    @Nonnull IICMultiblockBridge multiblock();

    @Nonnull IICIntegrationBridge integration();

    @Nonnull IICRdplBridge rdpl();
}
