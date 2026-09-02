package com.immersiveconvergence.mixin;

import com.immersiveconvergence.common.multiblock.IEMultiblock;
import com.immersiveconvergence.common.multiblock.IEMultiblockRegistry;

import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import flaxbeard.immersivepetroleum.common.blocks.metal.TileEntityDistillationTower;
import flaxbeard.immersivepetroleum.common.blocks.metal.TileEntityPumpjack;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin({TileEntityDistillationTower.class, TileEntityPumpjack.class})
public abstract class MixinIPMultiblockBounds {
    @Inject(method = "getBlockBounds", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectGetBlockBounds(CallbackInfoReturnable<float[]> cir) {
        TileEntityMultiblockPart<?> part = (TileEntityMultiblockPart<?>)(Object)this;
        IEMultiblock template = immersiveconvergence$template(part);
        if (template != null) { cir.setReturnValue(template.blockBoundsFor(part.pos, part.facing, part.mirrored)); }
    }

    @Inject(method = "getAdvancedSelectionBounds", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectSelectionBounds(CallbackInfoReturnable<List<AxisAlignedBB>> cir) {
        List<AxisAlignedBB> bounds = immersiveconvergence$bounds();
        if (bounds != null) { cir.setReturnValue(bounds); }
    }

    @Inject(method = "getAdvancedColisionBounds", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectCollisionBounds(CallbackInfoReturnable<List<AxisAlignedBB>> cir) {
        List<AxisAlignedBB> bounds = immersiveconvergence$bounds();
        if (bounds != null) { cir.setReturnValue(bounds); }
    }

    @Unique private List<AxisAlignedBB> immersiveconvergence$bounds() {
        TileEntityMultiblockPart<?> part = (TileEntityMultiblockPart<?>)(Object)this;
        IEMultiblock template = immersiveconvergence$template(part);
        return template == null ? null : template.boundsFor(part.pos, part.facing, part.mirrored, part.getPos());
    }

    @Unique private static IEMultiblock immersiveconvergence$template(TileEntityMultiblockPart<?> part) {
        if (part.pos < 0) { return null; }
        IEMultiblock template = IEMultiblockRegistry.templateFor(part);
        return template != null && template.hasShape() ? template : null;
    }
}
