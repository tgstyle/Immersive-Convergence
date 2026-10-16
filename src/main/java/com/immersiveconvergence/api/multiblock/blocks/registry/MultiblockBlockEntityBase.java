package com.immersiveconvergence.api.multiblock.blocks.registry;

import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockBEHelper;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockBE;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import javax.annotation.Nonnull;
import java.util.function.Function;

public abstract class MultiblockBlockEntityBase<State extends IMultiblockState, Helper extends IMultiblockBEHelper<State>> extends BlockEntity implements IMultiblockBE<State> {
    private final Helper helper;

    protected MultiblockBlockEntityBase(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState, Function<BlockEntity, Helper> makeHelper) {
        super(type, worldPosition, blockState);
        this.helper = makeHelper.apply(this);
    }

    @Override protected void loadAdditional(@Nonnull CompoundTag tag, @Nonnull Provider provider) {
        super.loadAdditional(tag, provider);
        helper.load(tag, provider);
    }

    @Override protected void saveAdditional(@Nonnull CompoundTag tag, @Nonnull Provider provider) {
        super.saveAdditional(tag, provider);
        helper.saveAdditional(tag, provider);
    }

    @Nullable @Override public Packet<ClientGamePacketListener> getUpdatePacket() { return helper.getUpdatePacket(); }

    @Nonnull @Override public CompoundTag getUpdateTag(@Nonnull Provider provider) { return helper.getUpdateTag(provider); }

    @Override public void handleUpdateTag(@Nonnull CompoundTag tag, @Nonnull Provider provider) { helper.handleUpdateTag(tag, provider); }

    @Override public void onDataPacket(@Nonnull Connection net, @Nonnull ClientboundBlockEntityDataPacket pkt, @Nonnull Provider provider) { helper.handleUpdateTag(pkt.getTag(), provider); }

    @Override public Helper getHelper() { return helper; }
}
