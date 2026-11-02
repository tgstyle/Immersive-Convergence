package com.immersiveconvergence.api.bridge;

import javax.annotation.Nonnull;

@SuppressWarnings("unused") public final class ICBridges {
    private static IICBridgeService service;
    private static IICClientBridge clientBridge;

    private ICBridges() {}

    public static void install(@Nonnull IICBridgeService installed) { service = installed; }

    public static void installClient(@Nonnull IICClientBridge installed) { clientBridge = installed; }

    @Nonnull public static IICTileBridge tile() { return required().tile(); }

    @Nonnull public static IICMultiblockBridge multiblock() { return required().multiblock(); }

    @Nonnull public static IICIntegrationBridge integration() { return required().integration(); }

    @Nonnull public static IICRdplBridge rdpl() { return required().rdpl(); }

    @Nonnull public static IICClientBridge client() {
        if (clientBridge == null) { throw new IllegalStateException("The client bridge exists only on a client, from preInit"); }
        return clientBridge;
    }

    @Nonnull private static IICBridgeService required() {
        if (service == null) { throw new IllegalStateException("The bridge service is installed at preInit"); }
        return service;
    }
}
