package com.immersiveconvergence.mixin.accessor.immersiveengineering;

import blusunrize.immersiveengineering.common.wires.WireSyncManager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = WireSyncManager.class, remap = false) public interface WireSyncManagerAccessor {
    @Accessor(value = "world", remap = false) Level immersiveconvergence$level();
}
