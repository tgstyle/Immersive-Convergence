package com.immersiveconvergence.mixin.ip.common;

import blusunrize.immersiveengineering.common.util.Utils;
import flaxbeard.immersivepetroleum.api.crafting.DistillationRecipe;
import flaxbeard.immersivepetroleum.common.blocks.metal.TileEntityDistillationTower;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileEntityDistillationTower.class)
public abstract class MixinIPTowerInput {
    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lblusunrize/immersiveengineering/common/util/Utils;drainFluidContainer(Lnet/minecraftforge/fluids/capability/IFluidHandler;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;", remap = false))
    private ItemStack redirectBucketDrain(IFluidHandler handler, ItemStack containerIn, ItemStack containerOut, EntityPlayer player) {
        FluidStack contained = FluidUtil.getFluidContained(containerIn);
        if (contained == null) { return Utils.drainFluidContainer(handler, containerIn, containerOut, player); }
        if (DistillationRecipe.findRecipe(Utils.copyFluidStackWithAmount(contained, Integer.MAX_VALUE, false)) == null) { return ItemStack.EMPTY; }
        FluidStack present = handler instanceof IFluidTank ? ((IFluidTank) handler).getFluid() : null;
        if (present != null && !present.isFluidEqual(contained)) { return ItemStack.EMPTY; }
        return Utils.drainFluidContainer(handler, containerIn, containerOut, player);
    }
}
