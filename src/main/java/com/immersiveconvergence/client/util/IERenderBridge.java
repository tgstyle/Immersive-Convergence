package com.immersiveconvergence.client.util;

import blusunrize.immersiveengineering.client.utils.IERenderTypes;
import net.minecraft.client.renderer.RenderType;

public final class IERenderBridge {
    private IERenderBridge() {}

    public static RenderType translucentFullbright() { return IERenderTypes.TRANSLUCENT_FULLBRIGHT; }
}
