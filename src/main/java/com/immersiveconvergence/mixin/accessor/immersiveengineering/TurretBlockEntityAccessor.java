package com.immersiveconvergence.mixin.accessor.immersiveengineering;

import blusunrize.immersiveengineering.common.blocks.metal.TurretBlockEntity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = TurretBlockEntity.class, remap = false) public interface TurretBlockEntityAccessor {
    @Accessor(value = "target", remap = false) LivingEntity ic$getTarget();
}
