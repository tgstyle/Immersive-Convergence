package com.immersiveconvergence.api.multiblock.blocks.registry;

import com.immersiveconvergence.api.block.ModProperties;
import com.immersiveconvergence.api.client.split.ISubmodelOffsetProvider;
import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistration;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockBEHelperDummy;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockBE;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.Vec3i;
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

public class MultiblockBlockEntityDummy<State extends IMultiblockState>
        extends BlockEntity
        implements ISubmodelOffsetProvider, IMultiblockBE<State> {
    private final IMultiblockBEHelperDummy<State> helper;

    public MultiblockBlockEntityDummy(
            BlockEntityType<?> type,
            BlockPos worldPosition,
            BlockState blockState,
            MultiblockRegistration<State> multiblock
    ) {
        super(type, worldPosition, blockState);
        this.helper = IMultiblockBEHelperDummy.MAKE_HELPER.get().makeFor(this, multiblock);
    }

    @Override protected void loadAdditional(@Nonnull CompoundTag tag, @Nonnull Provider provider) {
        super.loadAdditional(tag, provider);
        helper.load(tag, provider);
    }

    @Override protected void saveAdditional(@Nonnull CompoundTag tag, @Nonnull Provider provider) {
        super.saveAdditional(tag, provider);
        helper.saveAdditional(tag, provider);
    }

    @Nonnull @Override public CompoundTag getUpdateTag(@Nonnull Provider provider) { return helper.getUpdateTag(provider); }

    @Override public void handleUpdateTag(@Nonnull CompoundTag tag, @Nonnull Provider provider) { helper.handleUpdateTag(tag, provider); }

    @Override public void onDataPacket(@Nonnull Connection net, @Nonnull ClientboundBlockEntityDataPacket pkt, @Nonnull Provider provider) { helper.onDataPacket(pkt.getTag(), provider); }

    @Nullable @Override public Packet<ClientGamePacketListener> getUpdatePacket() { return helper.getUpdatePacket(); }

    @Override public IMultiblockBEHelperDummy<State> getHelper() { return helper; }

    @Override public BlockPos getModelOffset(BlockState state, Vec3i size) {
        BlockPos mirroredPosInMB = helper.getPositionInMB();
        if (helper.getMultiblock().mirrorable() && state.getValue(ModProperties.MIRRORED)) {
            mirroredPosInMB = new BlockPos(
                    size.getX() - mirroredPosInMB.getX() - 1,
                    mirroredPosInMB.getY(),
                    mirroredPosInMB.getZ()
            );
        }
        return mirroredPosInMB.subtract(helper.getMultiblock().masterPosInMB());
    }
}
