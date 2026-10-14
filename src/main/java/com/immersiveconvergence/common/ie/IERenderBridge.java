package com.immersiveconvergence.common.ie;

import blusunrize.immersiveengineering.api.IEApi;
import blusunrize.immersiveengineering.client.utils.IERenderTypes;
import net.minecraft.client.renderer.RenderType;

public final class IERenderBridge {
    private IERenderBridge() {}

    public static RenderType translucentFullbright() { return IERenderTypes.TRANSLUCENT_FULLBRIGHT; }

    public static void addRenderCacheClearer(Runnable clearer) { IEApi.renderCacheClearers.add(clearer); }
}
