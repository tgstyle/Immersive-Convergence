package com.immersiveconvergence.mixin;

import com.immersiveconvergence.common.multiblock.IEMultiblock;
import com.immersiveconvergence.common.multiblock.IEMultiblockRegistry;

import blusunrize.immersiveengineering.api.MultiblockHandler.IMultiblock;
import blusunrize.immersiveengineering.api.crafting.IngredientStack;
import blusunrize.immersiveengineering.common.blocks.multiblocks.MultiblockAlloySmelter;
import blusunrize.immersiveengineering.common.blocks.multiblocks.MultiblockAssembler;
import blusunrize.immersiveengineering.common.blocks.multiblocks.MultiblockAutoWorkbench;
import blusunrize.immersiveengineering.common.blocks.multiblocks.MultiblockBottlingMachine;
import blusunrize.immersiveengineering.common.blocks.multiblocks.MultiblockBucketWheel;
import blusunrize.immersiveengineering.common.blocks.multiblocks.MultiblockExcavator;
import blusunrize.immersiveengineering.common.blocks.multiblocks.MultiblockExcavatorDemo;
import blusunrize.immersiveengineering.common.blocks.multiblocks.MultiblockMetalPress;
import blusunrize.immersiveengineering.common.blocks.multiblocks.MultiblockArcFurnace;
import blusunrize.immersiveengineering.common.blocks.multiblocks.MultiblockBlastFurnace;
import blusunrize.immersiveengineering.common.blocks.multiblocks.MultiblockBlastFurnaceAdvanced;
import blusunrize.immersiveengineering.common.blocks.multiblocks.MultiblockCokeOven;
import blusunrize.immersiveengineering.common.blocks.multiblocks.MultiblockCrusher;
import blusunrize.immersiveengineering.common.blocks.multiblocks.MultiblockDieselGenerator;
import blusunrize.immersiveengineering.common.blocks.multiblocks.MultiblockFermenter;
import blusunrize.immersiveengineering.common.blocks.multiblocks.MultiblockLightningrod;
import blusunrize.immersiveengineering.common.blocks.multiblocks.MultiblockMixer;
import blusunrize.immersiveengineering.common.blocks.multiblocks.MultiblockRefinery;
import blusunrize.immersiveengineering.common.blocks.multiblocks.MultiblockSheetmetalTank;
import blusunrize.immersiveengineering.common.blocks.multiblocks.MultiblockSilo;
import blusunrize.immersiveengineering.common.blocks.multiblocks.MultiblockSqueezer;
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

@Mixin({MultiblockCokeOven.class, MultiblockBlastFurnace.class, MultiblockBlastFurnaceAdvanced.class, MultiblockAlloySmelter.class, MultiblockCrusher.class, MultiblockSqueezer.class, MultiblockFermenter.class, MultiblockMixer.class, MultiblockRefinery.class, MultiblockDieselGenerator.class, MultiblockLightningrod.class, MultiblockSheetmetalTank.class, MultiblockSilo.class, MultiblockArcFurnace.class, MultiblockAssembler.class, MultiblockAutoWorkbench.class, MultiblockBottlingMachine.class, MultiblockMetalPress.class, MultiblockBucketWheel.class, MultiblockExcavator.class, MultiblockExcavatorDemo.class})
public abstract class MixinIEMultiblock implements IMultiblock {
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
