package com.immersiveconvergence.mixin;

import com.immersiveconvergence.common.multiblock.IEMultiblock;
import com.immersiveconvergence.common.multiblock.IEMultiblockRegistry;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IAdvancedCollisionBounds;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IAdvancedSelectionBounds;
import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import org.spongepowered.asm.mixin.Mixin;
import java.util.ArrayList;
import java.util.List;

@Mixin(TileEntityMultiblockPart.class)
public abstract class MixinIETileEntityMultiblockPart implements IAdvancedSelectionBounds, IAdvancedCollisionBounds {
    @Override public List<AxisAlignedBB> getAdvancedSelectionBounds() { return immersiveconvergence$bounds(); }

    @Override public List<AxisAlignedBB> getAdvancedColisionBounds() { return immersiveconvergence$bounds(); }

    @Override public boolean isOverrideBox(AxisAlignedBB box, EntityPlayer player, RayTraceResult mop, ArrayList<AxisAlignedBB> list) { return false; }

    private List<AxisAlignedBB> immersiveconvergence$bounds() {
        TileEntityMultiblockPart<?> part = (TileEntityMultiblockPart<?>)(Object)this;
        if (part.pos < 0) { return null; }
        IEMultiblock template = IEMultiblockRegistry.templateFor(part);
        return template == null ? null : template.boundsFor(part.pos, part.facing, part.mirrored, part.getPos());
    }
}
