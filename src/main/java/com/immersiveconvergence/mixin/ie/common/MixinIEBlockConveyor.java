package com.immersiveconvergence.mixin.ie.common;

import blusunrize.immersiveengineering.common.blocks.metal.BlockConveyor;
import com.immersiveconvergence.core.ICMixinConfig;
import blusunrize.immersiveengineering.common.blocks.metal.BlockTypes_Conveyor;
import com.immersiveconvergence.common.blocks.conveyors.TileEntityConveyorBeltAlternative;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockConveyor.class)
public abstract class MixinIEBlockConveyor {
    @Inject(method = "createBasicTE(Lnet/minecraft/world/World;Lblusunrize/immersiveengineering/common/blocks/metal/BlockTypes_Conveyor;)Lnet/minecraft/tileentity/TileEntity;", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectCreateTE(World world, BlockTypes_Conveyor meta, CallbackInfoReturnable<TileEntity> cir) {
        if (ICMixinConfig.mixinSettings.replaceIEConveyors) { cir.setReturnValue(new TileEntityConveyorBeltAlternative()); }
    }
}
