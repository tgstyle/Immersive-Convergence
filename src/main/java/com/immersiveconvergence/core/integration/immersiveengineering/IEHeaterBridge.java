package com.immersiveconvergence.core.integration.immersiveengineering;

import blusunrize.immersiveengineering.api.tool.ExternalHeaterHandler;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class IEHeaterBridge {
    private IEHeaterBridge() {}

    public static int heatExternal(BlockEntity target, int energyAvailable, boolean redstone) {
        Level level = target.getLevel();
        if (level == null) { return 0; }
        ExternalHeaterHandler.IExternalHeatable heatable = level.getCapability(ExternalHeaterHandler.CAPABILITY, target.getBlockPos(), null);
        return heatable == null ? 0 : heatable.doHeatTick(energyAvailable, redstone);
    }
}
