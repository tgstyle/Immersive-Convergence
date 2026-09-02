package com.immersiveconvergence.mixin;

import com.immersiveconvergence.common.multiblock.IEMultiblock;
import com.immersiveconvergence.common.multiblock.IEMultiblockRegistry;

import blusunrize.immersiveengineering.api.MultiblockHandler.IMultiblock;
import blusunrize.immersiveengineering.api.crafting.IngredientStack;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.MultiblockDistillationTower;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.MultiblockPumpjack;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({MultiblockDistillationTower.class, MultiblockPumpjack.class})
public abstract class MixinIPMultiblock implements IMultiblock {
    @Inject(method = "isBlockTrigger", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectIsBlockTrigger(IBlockState state, CallbackInfoReturnable<Boolean> cir) {
        IEMultiblock template = IEMultiblockRegistry.get(getUniqueName());
        if (template != null && template.formable()) { cir.setReturnValue(template.isBlockTrigger(state)); }
    }

    @Inject(method = "createStructure", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectCreateStructure(World world, BlockPos pos, EnumFacing side, EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        IEMultiblock template = IEMultiblockRegistry.get(getUniqueName());
        if (template != null && template.formable()) { cir.setReturnValue(template.createStructure(world, pos, side, player)); }
    }

    @Inject(method = "getStructureManual", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectGetStructureManual(CallbackInfoReturnable<ItemStack[][][]> cir) {
        IEMultiblock template = IEMultiblockRegistry.get(getUniqueName());
        if (template != null) { cir.setReturnValue(template.getStructureManual()); }
    }

    @Inject(method = "getTotalMaterials", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectGetTotalMaterials(CallbackInfoReturnable<IngredientStack[]> cir) {
        IEMultiblock template = IEMultiblockRegistry.get(getUniqueName());
        if (template != null) { cir.setReturnValue(template.getTotalMaterials()); }
    }

    @Inject(method = "getManualScale", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectGetManualScale(CallbackInfoReturnable<Float> cir) {
        IEMultiblock template = IEMultiblockRegistry.get(getUniqueName());
        if (template != null) { cir.setReturnValue(template.getManualScale()); }
    }
}
