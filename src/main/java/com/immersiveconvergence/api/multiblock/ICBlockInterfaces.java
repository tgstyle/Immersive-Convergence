package com.immersiveconvergence.api.multiblock;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;

@SuppressWarnings("unused")
public class ICBlockInterfaces {
    public interface IBlockBounds {
        float[] getBlockBounds();
    }

    public interface ICollisionBounds extends IBlockBounds {
        List<AxisAlignedBB> getAdvancedCollisionBounds();
    }

    public interface ISelectionBounds extends IBlockBounds {
        List<AxisAlignedBB> getAdvancedSelectionBounds();

        boolean isOverrideBox(AxisAlignedBB var1, EntityPlayer var2, RayTraceResult var3, List<AxisAlignedBB> var4);
    }
}
