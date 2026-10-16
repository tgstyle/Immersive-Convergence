package com.immersiveconvergence.api.multiblock.blocks.env;

import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistration;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;
import com.immersiveconvergence.api.multiblock.blocks.util.RelativeBlockFace;
import com.immersiveconvergence.api.multiblock.blocks.util.ShapeType;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import javax.annotation.Nullable;

@NonExtendable public interface IMultiblockBEHelper<State extends IMultiblockState> {
    @Nullable State getState();

    @Nullable IMultiblockContext<State> getContext();

    void load(CompoundTag tag);

    void saveAdditional(CompoundTag tag);

    CompoundTag getUpdateTag();

    void handleUpdateTag(CompoundTag tag);

    Packet<ClientGamePacketListener> getUpdatePacket();

    <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side);

    MultiblockRegistration<State> getMultiblock();

    VoxelShape getShape(@Nullable CollisionContext ctx, ShapeType type);

    void disassemble();

    void markDisassembling();

    BlockPos getPositionInMB();

    InteractionResult click(Player player, InteractionHand hand, BlockHitResult hit);

    int getComparatorValue();

    void onNeighborChanged(BlockPos fromPos);

    int getRedstoneInput(RelativeBlockFace side);

    BlockState getOriginalBlock(Level level);

    ItemStack getPickBlock();
}
