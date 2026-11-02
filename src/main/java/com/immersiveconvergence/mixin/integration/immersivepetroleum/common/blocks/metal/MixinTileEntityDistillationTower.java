package com.immersiveconvergence.mixin.integration.immersivepetroleum.common.blocks.metal;

import com.immersiveconvergence.core.integration.immersiveengineering.util.IEMultiblockHooks;
import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import blusunrize.immersiveengineering.common.util.Utils;
import flaxbeard.immersivepetroleum.api.crafting.DistillationRecipe;
import flaxbeard.immersivepetroleum.common.blocks.metal.TileEntityDistillationTower;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityDistillationTower.class) public abstract class MixinTileEntityDistillationTower {
    @Inject(method = "getBlockBounds", at = @At("HEAD"), cancellable = true, remap = false, require = 1) private void injectGetBlockBounds(CallbackInfoReturnable<float[]> cir) { IEMultiblockHooks.shapedBlockBounds((TileEntityMultiblockPart<?>)(Object)this, cir); }

    @Inject(method = "getAdvancedSelectionBounds", at = @At("HEAD"), cancellable = true, remap = false, require = 1) private void injectSelectionBounds(CallbackInfoReturnable<List<AxisAlignedBB>> cir) { IEMultiblockHooks.shapedAdvancedBounds((TileEntityMultiblockPart<?>)(Object)this, cir); }

    @Inject(method = "getAdvancedColisionBounds", at = @At("HEAD"), cancellable = true, remap = false, require = 1) private void injectCollisionBounds(CallbackInfoReturnable<List<AxisAlignedBB>> cir) { IEMultiblockHooks.shapedAdvancedBounds((TileEntityMultiblockPart<?>)(Object)this, cir); }

    @Inject(method = "getEnergyPos", at = @At("HEAD"), cancellable = true, remap = false, require = 1) private void injectGetEnergyPos(CallbackInfoReturnable<int[]> cir) { IEMultiblockHooks.positions((TileEntityMultiblockPart<?>)(Object)this, "energy", cir); }

    @Inject(method = "getRedstonePos", at = @At("HEAD"), cancellable = true, remap = false, require = 1) private void injectGetRedstonePos(CallbackInfoReturnable<int[]> cir) { IEMultiblockHooks.positions((TileEntityMultiblockPart<?>)(Object)this, "redstone", cir); }

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lblusunrize/immersiveengineering/common/util/Utils;drainFluidContainer(Lnet/minecraftforge/fluids/capability/IFluidHandler;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;", remap = false), require = 0, expect = 0) private ItemStack redirectBucketDrain(IFluidHandler handler, ItemStack containerIn, ItemStack containerOut, EntityPlayer player) {
        FluidStack contained = FluidUtil.getFluidContained(containerIn);
        if (contained == null) { return Utils.drainFluidContainer(handler, containerIn, containerOut, player); }
        if (DistillationRecipe.findRecipe(Utils.copyFluidStackWithAmount(contained, Integer.MAX_VALUE, false)) == null) { return ItemStack.EMPTY; }
        FluidStack present = handler instanceof IFluidTank ? ((IFluidTank) handler).getFluid() : null;
        if (present != null && !present.isFluidEqual(contained)) { return ItemStack.EMPTY; }
        return Utils.drainFluidContainer(handler, containerIn, containerOut, player);
    }
}
