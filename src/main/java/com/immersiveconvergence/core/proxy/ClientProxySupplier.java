package com.immersiveconvergence.core.proxy;

public class ClientProxySupplier {
    public static CommonProxy get() {
        try {
            return (CommonProxy) Class.forName("com.immersiveconvergence.core.proxy.ClientProxy").getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
