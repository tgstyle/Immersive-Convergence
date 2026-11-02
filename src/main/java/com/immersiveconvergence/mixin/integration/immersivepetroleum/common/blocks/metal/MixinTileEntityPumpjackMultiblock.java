package com.immersiveconvergence.mixin.integration.immersivepetroleum.common.blocks.metal;

import com.immersiveconvergence.core.integration.immersiveengineering.util.IEMultiblockHooks;

import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import flaxbeard.immersivepetroleum.common.blocks.metal.TileEntityPumpjack;
import java.util.List;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityPumpjack.class) public abstract class MixinTileEntityPumpjackMultiblock {
    @Inject(method = "getBlockBounds", at = @At("HEAD"), cancellable = true, remap = false, require = 1) private void injectGetBlockBounds(CallbackInfoReturnable<float[]> cir) { IEMultiblockHooks.shapedBlockBounds((TileEntityMultiblockPart<?>)(Object)this, cir); }

    @Inject(method = "getAdvancedSelectionBounds", at = @At("HEAD"), cancellable = true, remap = false, require = 1) private void injectSelectionBounds(CallbackInfoReturnable<List<AxisAlignedBB>> cir) { IEMultiblockHooks.shapedAdvancedBounds((TileEntityMultiblockPart<?>)(Object)this, cir); }

    @Inject(method = "getAdvancedColisionBounds", at = @At("HEAD"), cancellable = true, remap = false, require = 1) private void injectCollisionBounds(CallbackInfoReturnable<List<AxisAlignedBB>> cir) { IEMultiblockHooks.shapedAdvancedBounds((TileEntityMultiblockPart<?>)(Object)this, cir); }

    @Inject(method = "getEnergyPos", at = @At("HEAD"), cancellable = true, remap = false, require = 1) private void injectGetEnergyPos(CallbackInfoReturnable<int[]> cir) { IEMultiblockHooks.positions((TileEntityMultiblockPart<?>)(Object)this, "energy", cir); }

    @Inject(method = "getRedstonePos", at = @At("HEAD"), cancellable = true, remap = false, require = 1) private void injectGetRedstonePos(CallbackInfoReturnable<int[]> cir) { IEMultiblockHooks.positions((TileEntityMultiblockPart<?>)(Object)this, "redstone", cir); }
}
