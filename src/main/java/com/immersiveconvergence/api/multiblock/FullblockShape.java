package com.immersiveconvergence.api.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.function.Function;

public class FullblockShape implements Function<BlockPos, VoxelShape> {
    public static final FullblockShape GETTER = new FullblockShape();

    @Override public VoxelShape apply(BlockPos posInMultiblock) { return Shapes.block(); }
}
