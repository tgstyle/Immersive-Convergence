package com.immersiveconvergence.mixin.ie.common;

import blusunrize.immersiveengineering.common.blocks.metal.TileEntityTurret;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TileEntityTurret.class)
public interface MixinIEComputerTurret {
    @Accessor(value="target", remap=false)
    EntityLivingBase getTarget();
}
