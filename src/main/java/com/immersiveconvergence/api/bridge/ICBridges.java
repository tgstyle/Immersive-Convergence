package com.immersiveconvergence.api.bridge;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public final class ICBridges {
    private static volatile IICBridgeService service;
    private static IICClientBridge clientBridge;

    private ICBridges() {}

    public static void install(@Nonnull IICBridgeService installed) { service = installed; }

    public static void installClient(@Nonnull IICClientBridge installed) { clientBridge = installed; }

    public static boolean installed() { return service != null; }

    @Nonnull public static IICMultiblockBridge multiblock() { return required().multiblock(); }

    @Nonnull public static IICIntegrationBridge integration() { return required().integration(); }

    @Nonnull public static IICClientBridge client() {
        if (clientBridge == null) { throw new IllegalStateException("The client bridge exists only on a client, from client setup"); }
        return clientBridge;
    }

    @Nonnull private static IICBridgeService required() {
        IICBridgeService current = service;
        if (current == null) { throw new IllegalStateException("The bridge service is installed at mod construction"); }
        return current;
    }
}
