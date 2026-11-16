package com.immersiveconvergence.core.integration.immersiveengineering;

import com.immersiveconvergence.api.multiblock.util.CapabilityUtils;

import blusunrize.immersiveengineering.api.tool.ExternalHeaterHandler;
import blusunrize.immersiveengineering.api.tool.ExternalHeaterHandler.IExternalHeatable;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class IEHeaterBridge {
    private IEHeaterBridge() {}

    public static int heatExternal(BlockEntity target, int energyAvailable, boolean redstone) {
        IExternalHeatable heatable = CapabilityUtils.getCapability(target, ExternalHeaterHandler.CAPABILITY, null);
        return heatable == null ? 0 : heatable.doHeatTick(energyAvailable, redstone);
    }
}
