package com.immersiveconvergence.api.multiblock.blocks.logic;

import com.immersiveconvergence.api.multiblock.blocks.component.IMultiblockComponent;
import com.immersiveconvergence.api.multiblock.blocks.env.IInitialMultiblockContext;
import com.immersiveconvergence.api.multiblock.blocks.util.ShapeType;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.function.Function;

public interface IMultiblockLogic<State extends IMultiblockState> extends IMultiblockComponent<State> {
    State createInitialState(IInitialMultiblockContext<State> capabilitySource);

    Function<BlockPos, VoxelShape> shapeGetter(ShapeType forType);
}
