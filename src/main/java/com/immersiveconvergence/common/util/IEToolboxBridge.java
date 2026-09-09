package com.immersiveconvergence.common.util;

import com.immersiveconvergence.api.IICTool;

import blusunrize.immersiveengineering.api.tool.ToolboxHandler;

public final class IEToolboxBridge {
    private IEToolboxBridge() {}

    public static void registerToolType() { ToolboxHandler.addToolType(stack -> stack.getItem() instanceof IICTool && ((IICTool)stack.getItem()).isTool(stack)); }
}
