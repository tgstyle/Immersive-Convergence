package com.immersiveconvergence.api.wires.connectors;

import com.immersiveconvergence.api.block.BlockContext;
import com.immersiveconvergence.api.block.ICItemBase;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.api.wires.ICWires;
import com.immersiveconvergence.api.wires.IICWireCoil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

@SuppressWarnings("unused")
public class ICItemWireCoil extends ICItemBase implements IICWireCoil {
    private final List<ICWireType> types;

    public ICItemWireCoil(BlockContext context, String name, List<ICWireType> types) {
        super(context, name, 64, names(types));
        this.types = types;
    }

    @Override @Nonnull public ICWireType getWireType(@Nonnull ItemStack stack) { return types.get(Math.max(0, Math.min(stack.getMetadata(), types.size() - 1))); }

    @Override @Nonnull public EnumActionResult onItemUse(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) { return ICWires.useCoil(player, world, pos, hand, facing, hitX, hitY, hitZ); }

    private static String[] names(List<ICWireType> types) {
        String[] names = new String[types.size()];
        for (int meta = 0; meta < names.length; meta++) { names[meta] = types.get(meta).getUniqueName(); }
        return names;
    }
}
