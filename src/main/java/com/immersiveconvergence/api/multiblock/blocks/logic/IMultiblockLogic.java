package com.immersiveconvergence.api.multiblock.blocks.logic;

import com.immersiveconvergence.api.multiblock.blocks.component.IMultiblockComponent;
import com.immersiveconvergence.api.multiblock.blocks.env.IInitialMultiblockContext;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockContext;
import com.immersiveconvergence.api.multiblock.blocks.util.ShapeType;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.function.Function;

@SuppressWarnings({"unused", "RedundantSuppression"}) public interface IMultiblockLogic<State extends IMultiblockState> extends IMultiblockComponent<State> {
    State createInitialState(IInitialMultiblockContext<State> capabilitySource);

    Function<BlockPos, VoxelShape> shapeGetter(ShapeType forType);

    default VoxelShape postProcessAbsoluteShape(
            IMultiblockContext<State> ctx,
            VoxelShape defaultShape,
            CollisionContext shapeCtx,
            BlockPos posInMultiblock,
            ShapeType type
    ) { return defaultShape; }
}
