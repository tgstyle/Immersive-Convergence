package com.immersiveconvergence.mixin.integration.immersiveengineering.common.blocks.metal;

import com.immersiveconvergence.core.integration.immersiveengineering.IEMultiblockRegistry;
import com.immersiveconvergence.core.integration.immersiveengineering.util.IEMultiblockHooks;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IAdvancedCollisionBounds;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IAdvancedSelectionBounds;
import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntitySheetmetalTank;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntitySheetmetalTank.class) public abstract class MixinTileEntitySheetmetalTank implements IAdvancedSelectionBounds, IAdvancedCollisionBounds {
    @Inject(method = "getBlockBounds", at = @At("HEAD"), cancellable = true, remap = false, require = 0) private void injectGetBlockBounds(CallbackInfoReturnable<float[]> cir) { IEMultiblockHooks.blockBounds((TileEntityMultiblockPart<?>)(Object)this, cir); }

    @Redirect(method = "canDrainTankFrom", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntitySheetmetalTank;pos:I", remap = true), remap = false) private int redirectPosCanDrainTankFrom(TileEntitySheetmetalTank part) { return IEMultiblockRegistry.portPos(part); }

    @Redirect(method = "canFillTankFrom", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntitySheetmetalTank;pos:I", remap = true), remap = false) private int redirectPosCanFillTankFrom(TileEntitySheetmetalTank part) { return IEMultiblockRegistry.portPos(part); }

    @Redirect(method = "getAccessibleFluidTanks", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntitySheetmetalTank;pos:I", remap = true), remap = false) private int redirectPosGetAccessibleFluidTanks(TileEntitySheetmetalTank part) { return IEMultiblockRegistry.portPos(part); }

    @Override @Nonnull public List<AxisAlignedBB> getAdvancedSelectionBounds() { return IEMultiblockHooks.partBounds((TileEntityMultiblockPart<?>)(Object)this); }

    @Override @Nonnull public List<AxisAlignedBB> getAdvancedColisionBounds() { return IEMultiblockHooks.partBounds((TileEntityMultiblockPart<?>)(Object)this); }

    @Override public boolean isOverrideBox(@Nonnull AxisAlignedBB box, @Nonnull EntityPlayer player, @Nonnull RayTraceResult mop, @Nonnull ArrayList<AxisAlignedBB> list) { return false; }

    @Inject(method = "getOriginalBlock", at = @At("HEAD"), cancellable = true, remap = false, require = 0) private void injectGetOriginalBlock(CallbackInfoReturnable<ItemStack> cir) { IEMultiblockHooks.originalBlock((TileEntityMultiblockPart<?>)(Object)this, cir); }
}
