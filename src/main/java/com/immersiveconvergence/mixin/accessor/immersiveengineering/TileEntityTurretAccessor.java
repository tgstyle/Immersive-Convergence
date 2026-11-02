package com.immersiveconvergence.mixin.accessor.immersiveengineering;

import blusunrize.immersiveengineering.common.blocks.metal.TileEntityTurret;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TileEntityTurret.class) public interface TileEntityTurretAccessor {
    @Accessor(value="target", remap=false) EntityLivingBase getTarget();
}
