package com.immersiveconvergence.client;

import com.immersiveconvergence.ImmersiveConvergence;

import blusunrize.immersiveengineering.client.models.obj.IEOBJLoader;

public final class IEModelSupport {
    private IEModelSupport() {}

    public static void addObjDomain() { IEOBJLoader.instance.addDomain(ImmersiveConvergence.MODID); }
}
