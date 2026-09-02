package com.immersiveconvergence.mixin;

import com.immersiveconvergence.common.multiblock.IEMultiblock;
import com.immersiveconvergence.common.multiblock.IEMultiblockRegistry;

import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityArcFurnace;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityCrusher;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityDieselGenerator;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityExcavator;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityFermenter;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityMixer;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityRefinery;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntitySqueezer;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin({TileEntityArcFurnace.class, TileEntityCrusher.class, TileEntityDieselGenerator.class, TileEntityExcavator.class, TileEntityFermenter.class, TileEntityMixer.class, TileEntityRefinery.class, TileEntitySqueezer.class})
public abstract class MixinIEMultiblockAdvancedBounds {
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
        if (part.pos < 0) { return null; }
        IEMultiblock template = IEMultiblockRegistry.templateFor(part);
        return template == null ? null : template.boundsFor(part.pos, part.facing, part.mirrored, part.getPos());
    }
}
