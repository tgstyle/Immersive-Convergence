package com.immersiveconvergence.mixin.integration.immersiveengineering.common.blocks.metal;

import com.immersiveconvergence.core.integration.immersiveengineering.IEMultiblockRegistry;
import com.immersiveconvergence.core.integration.immersiveengineering.util.IEMultiblockHooks;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IAdvancedCollisionBounds;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IAdvancedSelectionBounds;
import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityAssembler;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityAssembler.class) public abstract class MixinTileEntityAssembler implements IAdvancedSelectionBounds, IAdvancedCollisionBounds {
    @Inject(method = "getBlockBounds", at = @At("HEAD"), cancellable = true, remap = false, require = 0) private void injectGetBlockBounds(CallbackInfoReturnable<float[]> cir) { IEMultiblockHooks.blockBounds((TileEntityMultiblockPart<?>)(Object)this, cir); }

    @Inject(method = "getEnergyPos", at = @At("HEAD"), cancellable = true, remap = false, require = 0) private void injectGetEnergyPos(CallbackInfoReturnable<int[]> cir) { IEMultiblockHooks.positions((TileEntityMultiblockPart<?>)(Object)this, "energy", cir); }

    @Inject(method = "getRedstonePos", at = @At("HEAD"), cancellable = true, remap = false, require = 0) private void injectGetRedstonePos(CallbackInfoReturnable<int[]> cir) { IEMultiblockHooks.positions((TileEntityMultiblockPart<?>)(Object)this, "redstone", cir); }

    @Redirect(method = "getCapability", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntityAssembler;pos:I", remap = true), remap = false) private int redirectPosGetCapability(TileEntityAssembler part) { return IEMultiblockRegistry.portPos(part); }

    @Redirect(method = "hasCapability", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntityAssembler;pos:I", remap = true), remap = false) private int redirectPosHasCapability(TileEntityAssembler part) { return IEMultiblockRegistry.portPos(part); }

    @Redirect(method = "getAccessibleFluidTanks", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntityAssembler;pos:I", remap = true), remap = false) private int redirectPosGetAccessibleFluidTanks(TileEntityAssembler part) { return IEMultiblockRegistry.portPos(part); }

    @Redirect(method = "sigOutputDirections", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntityAssembler;pos:I", remap = true), remap = false) private int redirectPosSigOutputDirections(TileEntityAssembler part) { return IEMultiblockRegistry.portPos(part); }

    @Override @Nonnull public List<AxisAlignedBB> getAdvancedSelectionBounds() { return IEMultiblockHooks.partBounds((TileEntityMultiblockPart<?>)(Object)this); }

    @Override @Nonnull public List<AxisAlignedBB> getAdvancedColisionBounds() { return IEMultiblockHooks.partBounds((TileEntityMultiblockPart<?>)(Object)this); }

    @Override public boolean isOverrideBox(@Nonnull AxisAlignedBB box, @Nonnull EntityPlayer player, @Nonnull RayTraceResult mop, @Nonnull ArrayList<AxisAlignedBB> list) { return false; }
}
