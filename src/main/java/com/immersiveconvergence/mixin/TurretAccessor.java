package com.immersiveconvergence.mixin;

import blusunrize.immersiveengineering.common.blocks.metal.TurretBlockEntity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = TurretBlockEntity.class, remap = false)
public interface TurretAccessor {
    @Accessor(value = "target", remap = false) LivingEntity ic$getTarget();
}
