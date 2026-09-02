package com.immersiveconvergence.common.multiblock;

import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.blocks.BlockIPMetalMultiblocks;
import flaxbeard.immersivepetroleum.common.blocks.metal.BlockTypes_IPMetalMultiblock;
import flaxbeard.immersivepetroleum.common.blocks.metal.TileEntityDistillationTower;
import flaxbeard.immersivepetroleum.common.blocks.metal.TileEntityPumpjack;
import net.minecraft.block.state.IBlockState;
import java.util.function.Supplier;

public final class IPMultiblocks {
    private IPMultiblocks() {}

    private static Supplier<IBlockState> state(BlockTypes_IPMetalMultiblock type) {
        return () -> {
            BlockIPMetalMultiblocks block = (BlockIPMetalMultiblocks)IPContent.blockMetalMultiblock;
            return block.getDefaultState().withProperty(block.property, type);
        };
    }

    public static void init() {
        IEMultiblockRegistry.register("immersivepetroleum", "IP:DistillationTower", "distillation_tower",
                state(BlockTypes_IPMetalMultiblock.DISTILLATION_TOWER), state(BlockTypes_IPMetalMultiblock.DISTILLATION_TOWER_PARENT),
                IEMultiblock.Anchor.SIDE_RAW, true);
        IEMultiblockRegistry.register("immersivepetroleum", "IP:Pumpjack", "pumpjack",
                state(BlockTypes_IPMetalMultiblock.PUMPJACK), state(BlockTypes_IPMetalMultiblock.PUMPJACK_PARENT),
                IEMultiblock.Anchor.SIDE, true);

        IEMultiblockRegistry.registerTile(TileEntityDistillationTower.class, "IP:DistillationTower");
        IEMultiblockRegistry.registerTile(TileEntityPumpjack.class, "IP:Pumpjack");
    }
}
