package com.immersiveconvergence.api.wires.connectors;

import com.immersiveconvergence.api.block.ModEntityBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import javax.annotation.Nonnull;
import java.util.function.BiFunction;

public abstract class ICConnectorBlock<T extends ICConnectableBlockEntity> extends ModEntityBlock<T> {
    protected ICConnectorBlock(BiFunction<BlockPos, BlockState, T> makeEntity) { super(makeEntity, connectorProperties()); }

    @Override protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.FACING);
    }

    @Override protected Direction getDefaultFacing() { return Direction.DOWN; }

    private static BlockBehaviour.Properties connectorProperties() { return BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(3.0F, 9.0F).requiresCorrectToolForDrops().noOcclusion().dynamicShape(); }

}
