package com.immersiveconvergence.api.wires.connectors;

import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.api.wires.ICWires;
import com.immersiveconvergence.api.wires.IICWireCoil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class ICWireCoilItem extends Item implements IICWireCoil {
    private final ICWireType type;

    public ICWireCoilItem(@Nonnull ICWireType type) {
        super(new Item.Properties());
        this.type = type;
    }

    @Override @Nonnull public ICWireType getWireType(@Nonnull ItemStack stack) { return type; }

    @Override @Nonnull public InteractionResult useOn(@Nonnull UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) { return InteractionResult.PASS; }
        BlockPos pos = context.getClickedPos();
        Vec3 hit = context.getClickLocation();
        return ICWires.useCoil(player, context.getLevel(), pos, context.getHand(), (float)(hit.x - pos.getX()), (float)(hit.y - pos.getY()), (float)(hit.z - pos.getZ()));
    }
}
