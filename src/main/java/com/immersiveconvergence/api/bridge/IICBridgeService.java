package com.immersiveconvergence.api.bridge;

import javax.annotation.Nonnull;

public interface IICBridgeService {
    @Nonnull IICMultiblockBridge multiblock();

    @Nonnull IICIntegrationBridge integration();
}
