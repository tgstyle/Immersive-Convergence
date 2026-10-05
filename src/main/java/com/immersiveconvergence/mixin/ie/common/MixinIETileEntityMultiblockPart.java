package com.immersiveconvergence.mixin.ie.common;

import com.immersiveconvergence.common.multiblock.IEMultiblock;
import com.immersiveconvergence.common.multiblock.IEMultiblockRegistry;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IAdvancedCollisionBounds;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IAdvancedSelectionBounds;
import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityAssembler;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityAutoWorkbench;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityBottlingMachine;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityBucketWheel;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityLightningrod;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityMetalPress;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntitySheetmetalTank;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntitySilo;
import blusunrize.immersiveengineering.common.blocks.stone.TileEntityAlloySmelter;
import blusunrize.immersiveengineering.common.blocks.stone.TileEntityBlastFurnace;
import blusunrize.immersiveengineering.common.blocks.stone.TileEntityCokeOven;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin({TileEntityAlloySmelter.class, TileEntityAssembler.class, TileEntityAutoWorkbench.class, TileEntityBlastFurnace.class, TileEntityBottlingMachine.class, TileEntityBucketWheel.class, TileEntityCokeOven.class, TileEntityLightningrod.class, TileEntityMetalPress.class, TileEntitySheetmetalTank.class, TileEntitySilo.class})
public abstract class MixinIETileEntityMultiblockPart implements IAdvancedSelectionBounds, IAdvancedCollisionBounds {
    @Override @Nonnull public List<AxisAlignedBB> getAdvancedSelectionBounds() { return immersiveconvergence$bounds(); }

    @Override @Nonnull public List<AxisAlignedBB> getAdvancedColisionBounds() { return immersiveconvergence$bounds(); }

    @Override public boolean isOverrideBox(@Nonnull AxisAlignedBB box, @Nonnull EntityPlayer player, @Nonnull RayTraceResult mop, @Nonnull ArrayList<AxisAlignedBB> list) { return false; }

    @Unique @Nonnull private List<AxisAlignedBB> immersiveconvergence$bounds() {
        TileEntityMultiblockPart<?> part = (TileEntityMultiblockPart<?>)(Object)this;
        if (part.pos < 0) { return Collections.emptyList(); }
        IEMultiblock template = IEMultiblockRegistry.templateFor(part);
        return template == null ? Collections.emptyList() : template.boundsFor(part.pos, part.facing, part.mirrored, part.getPos());
    }
}
