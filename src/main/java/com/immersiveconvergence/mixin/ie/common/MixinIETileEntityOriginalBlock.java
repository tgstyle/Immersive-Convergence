package com.immersiveconvergence.mixin.ie.common;

import com.immersiveconvergence.common.multiblock.IEMultiblock;
import com.immersiveconvergence.common.multiblock.IEMultiblockRegistry;

import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityBucketWheel;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityLightningrod;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntitySheetmetalTank;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntitySilo;
import blusunrize.immersiveengineering.common.blocks.stone.TileEntityAlloySmelter;
import blusunrize.immersiveengineering.common.blocks.stone.TileEntityBlastFurnace;
import blusunrize.immersiveengineering.common.blocks.stone.TileEntityBlastFurnaceAdvanced;
import blusunrize.immersiveengineering.common.blocks.stone.TileEntityCokeOven;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({TileEntityAlloySmelter.class, TileEntityBlastFurnace.class, TileEntityBlastFurnaceAdvanced.class, TileEntityBucketWheel.class, TileEntityCokeOven.class, TileEntityLightningrod.class, TileEntitySheetmetalTank.class, TileEntitySilo.class})
public abstract class MixinIETileEntityOriginalBlock {
    @Inject(method = "getOriginalBlock", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectGetOriginalBlock(CallbackInfoReturnable<ItemStack> cir) {
        TileEntityMultiblockPart<?> part = (TileEntityMultiblockPart<?>)(Object)this;
        IEMultiblock template = IEMultiblockRegistry.templateFor(part);
        ItemStack stack = template == null ? null : template.getOriginalBlock(part.pos);
        if (stack != null) { cir.setReturnValue(stack); }
    }
}
