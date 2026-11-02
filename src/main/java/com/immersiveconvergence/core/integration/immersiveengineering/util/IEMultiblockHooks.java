package com.immersiveconvergence.core.integration.immersiveengineering.util;

import com.immersiveconvergence.core.integration.immersiveengineering.IEMultiblock;
import com.immersiveconvergence.core.integration.immersiveengineering.IEMultiblockRegistry;

import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public final class IEMultiblockHooks {
    private IEMultiblockHooks() {}

    public static void blockBounds(TileEntityMultiblockPart<?> part, CallbackInfoReturnable<float[]> cir) {
        if (part.pos < 0) { return; }
        IEMultiblock template = IEMultiblockRegistry.templateFor(part);
        if (template != null) { cir.setReturnValue(template.blockBoundsFor(part.pos, part.facing, part.mirrored)); }
    }

    public static void advancedBounds(TileEntityMultiblockPart<?> part, CallbackInfoReturnable<List<AxisAlignedBB>> cir) {
        if (part.pos < 0) { return; }
        IEMultiblock template = IEMultiblockRegistry.templateFor(part);
        List<AxisAlignedBB> bounds = template == null ? null : template.boundsFor(part.pos, part.facing, part.mirrored, part.getPos());
        if (bounds != null) { cir.setReturnValue(bounds); }
    }

    public static void shapedBlockBounds(TileEntityMultiblockPart<?> part, CallbackInfoReturnable<float[]> cir) {
        IEMultiblock template = shapedTemplate(part);
        if (template != null) { cir.setReturnValue(template.blockBoundsFor(part.pos, part.facing, part.mirrored)); }
    }

    public static void shapedAdvancedBounds(TileEntityMultiblockPart<?> part, CallbackInfoReturnable<List<AxisAlignedBB>> cir) {
        IEMultiblock template = shapedTemplate(part);
        List<AxisAlignedBB> bounds = template == null ? null : template.boundsFor(part.pos, part.facing, part.mirrored, part.getPos());
        if (bounds != null) { cir.setReturnValue(bounds); }
    }

    public static void positions(TileEntityMultiblockPart<?> part, String prefix, CallbackInfoReturnable<int[]> cir) {
        IEMultiblock template = IEMultiblockRegistry.templateFor(part);
        int[] positions = template == null ? null : template.positionsNamed(prefix);
        if (positions != null) { cir.setReturnValue(positions); }
    }

    public static void originalBlock(TileEntityMultiblockPart<?> part, CallbackInfoReturnable<ItemStack> cir) {
        IEMultiblock template = IEMultiblockRegistry.templateFor(part);
        ItemStack stack = template == null ? null : template.getOriginalBlock(part.pos);
        if (stack != null) { cir.setReturnValue(stack); }
    }

    @Nonnull public static List<AxisAlignedBB> partBounds(TileEntityMultiblockPart<?> part) {
        if (part.pos < 0) { return Collections.emptyList(); }
        IEMultiblock template = IEMultiblockRegistry.templateFor(part);
        return template == null ? Collections.emptyList() : template.boundsFor(part.pos, part.facing, part.mirrored, part.getPos());
    }

    private static IEMultiblock shapedTemplate(TileEntityMultiblockPart<?> part) {
        if (part.pos < 0) { return null; }
        IEMultiblock template = IEMultiblockRegistry.templateFor(part);
        return template != null && template.hasShape() ? template : null;
    }
}
