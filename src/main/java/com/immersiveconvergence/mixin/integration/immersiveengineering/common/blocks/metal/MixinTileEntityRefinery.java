package com.immersiveconvergence.mixin.integration.immersiveengineering.common.blocks.metal;

import com.immersiveconvergence.core.integration.immersiveengineering.IEMultiblockRegistry;
import com.immersiveconvergence.core.integration.immersiveengineering.util.IEMultiblockHooks;
import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityRefinery;
import java.util.List;
import net.minecraft.util.math.AxisAlignedBB;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityRefinery.class) public abstract class MixinTileEntityRefinery {
    @Inject(method = "getAdvancedSelectionBounds", at = @At("HEAD"), cancellable = true, remap = false, require = 0) private void injectSelectionBounds(CallbackInfoReturnable<List<AxisAlignedBB>> cir) { IEMultiblockHooks.advancedBounds((TileEntityMultiblockPart<?>)(Object)this, cir); }

    @Inject(method = "getAdvancedColisionBounds", at = @At("HEAD"), cancellable = true, remap = false, require = 0) private void injectCollisionBounds(CallbackInfoReturnable<List<AxisAlignedBB>> cir) { IEMultiblockHooks.advancedBounds((TileEntityMultiblockPart<?>)(Object)this, cir); }

    @Inject(method = "getBlockBounds", at = @At("HEAD"), cancellable = true, remap = false, require = 0) private void injectGetBlockBounds(CallbackInfoReturnable<float[]> cir) { IEMultiblockHooks.blockBounds((TileEntityMultiblockPart<?>)(Object)this, cir); }

    @Inject(method = "getEnergyPos", at = @At("HEAD"), cancellable = true, remap = false, require = 0) private void injectGetEnergyPos(CallbackInfoReturnable<int[]> cir) { IEMultiblockHooks.positions((TileEntityMultiblockPart<?>)(Object)this, "energy", cir); }

    @Inject(method = "getRedstonePos", at = @At("HEAD"), cancellable = true, remap = false, require = 0) private void injectGetRedstonePos(CallbackInfoReturnable<int[]> cir) { IEMultiblockHooks.positions((TileEntityMultiblockPart<?>)(Object)this, "redstone", cir); }

    @Redirect(method = "canDrainTankFrom", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntityRefinery;pos:I", remap = true), remap = false) private int redirectPosCanDrainTankFrom(TileEntityRefinery part) { return IEMultiblockRegistry.portPos(part); }

    @Redirect(method = "canFillTankFrom", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntityRefinery;pos:I", remap = true), remap = false) private int redirectPosCanFillTankFrom(TileEntityRefinery part) { return IEMultiblockRegistry.portPos(part); }

    @Redirect(method = "getAccessibleFluidTanks", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntityRefinery;pos:I", remap = true), remap = false) private int redirectPosGetAccessibleFluidTanks(TileEntityRefinery part) { return IEMultiblockRegistry.portPos(part); }
}
