package com.immersiveconvergence.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class HammerUse {
    public static InteractionResult onBlock(Level world, BlockPos pos, Direction side, @Nullable Player player, InteractionHand hand, Vec3 clickLocation) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof BlockInterfaces.IConfigurableSides sideConfig) {
            Direction activeSide = (player != null && player.isShiftKeyDown()) ? side.getOpposite() : side;
            return sideConfig.toggleSide(activeSide, player) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        }
        boolean rotate = !(tile instanceof BlockInterfaces.IDirectionalBE) && !(tile instanceof BlockInterfaces.IHammerInteraction);
        if (!rotate && tile instanceof BlockInterfaces.IDirectionalBE dirBE) { rotate = dirBE.canHammerRotate(side, clickLocation.subtract(Vec3.atLowerCornerOf(pos)), player); }
        if (rotate && BlockRotationUtil.rotateBlock(world, pos, player != null && (player.isShiftKeyDown() != side.equals(Direction.DOWN)))) { return InteractionResult.SUCCESS; }
        else if (!rotate && tile instanceof BlockInterfaces.IHammerInteraction hammerInteraction && hammerInteraction.hammerUseSide(side, player, hand, clickLocation)) { return InteractionResult.SUCCESS; }
        return InteractionResult.PASS;
    }

    public static InteractionResult onEntity(Player player, LivingEntity entity) {
        if (!player.level().isClientSide && BlockRotationUtil.rotateEntity(entity)) { return InteractionResult.SUCCESS; }
        return InteractionResult.PASS;
    }
}
