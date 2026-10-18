package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.client.split.ISubmodelOffsetProvider;
import com.immersiveconvergence.api.block.ModProperties;
import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistration;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockBEHelper;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class MachineBlockEntityCommon<State extends IMultiblockState> implements ISubmodelOffsetProvider {
    private final MultiblockRegistration<State> multiblock;
    private final Supplier<IMultiblockBEHelper<State>> helperSupplier;

    public MachineBlockEntityCommon(MultiblockRegistration<State> multiblock, Supplier<IMultiblockBEHelper<State>> helperSupplier) {
        this.multiblock = multiblock;
        this.helperSupplier = helperSupplier;
    }

    public Stream<ItemStack> getDroppedItems() {
        List<ItemStack> drops = new ArrayList<>();
        State state = helperSupplier.get().getState();
        if (state != null) { multiblock.logic().dropExtraItems(state, drops::add); }
        return drops.stream();
    }

    @Override public BlockPos getModelOffset(BlockState state, Vec3i size) {
        BlockPos offset = helperSupplier.get().getPositionInMB().subtract(multiblock.masterPosInMB());
        if (state != null && state.hasProperty(ModProperties.MIRRORED) && state.getValue(ModProperties.MIRRORED)) { return new BlockPos(-offset.getX(), offset.getY(), offset.getZ()); }
        return offset;
    }
}
