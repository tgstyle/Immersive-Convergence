package com.immersiveconvergence.core.compat.opencomputers.ie;

import li.cil.oc.api.Driver;
import java.util.Map;

public final class IEOpenComputers {
    private IEOpenComputers() {}

    public static void register(Map<String, String> names) { Driver.add(new IEBlockDriver(names)); }
}
