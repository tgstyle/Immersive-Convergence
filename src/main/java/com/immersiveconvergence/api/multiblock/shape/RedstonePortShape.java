package com.immersiveconvergence.api.multiblock.shape;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.Set;
import java.util.function.Function;

public final class RedstonePortShape implements Function<BlockPos, VoxelShape> {
    private final Function<BlockPos, VoxelShape> base;
    private final Set<BlockPos> ports;

    public RedstonePortShape(Function<BlockPos, VoxelShape> base, Set<BlockPos> ports) {
        this.base = base;
        this.ports = ports;
    }

    @Override public VoxelShape apply(BlockPos posInMultiblock) { return base.apply(posInMultiblock); }

    public boolean isPort(BlockPos posInMultiblock) { return ports.contains(posInMultiblock); }
}
