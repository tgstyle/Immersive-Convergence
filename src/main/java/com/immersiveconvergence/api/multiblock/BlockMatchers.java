package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.multiblock.BlockMatcher.Result;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import java.util.List;

final class BlockMatchers {
    private static final List<String> METALS = List.of("copper", "aluminum", "lead", "silver", "nickel", "uranium", "constantan", "electrum", "steel", "iron", "gold");
    private static final List<BooleanProperty> SIDES = List.of(CrossCollisionBlock.NORTH, CrossCollisionBlock.EAST, CrossCollisionBlock.SOUTH, CrossCollisionBlock.WEST);

    private BlockMatchers() {}

    static void init() {
        BlockMatcher.addPredicate((expected, found, world, pos) -> expected == found ? Result.allow(1) : Result.deny(1));
        BlockMatcher.addPreprocessor((expected, found, world, pos) -> {
            if (expected.getBlock() instanceof CrossCollisionBlock && expected.getBlock() == found.getBlock()) {
                for (BooleanProperty side : SIDES) {
                    if (!expected.getValue(side)) { found = found.setValue(side, false); }
                }
            }
            return found;
        });
        List<TagKey<Block>> genericTags = genericTags();
        BlockMatcher.addPredicate((expected, found, world, pos) -> {
            if (expected.getBlock() != found.getBlock()) {
                for (TagKey<Block> t : genericTags) {
                    if (expected.is(t) && found.is(t)) { return Result.allow(2); }
                }
            }
            return Result.DEFAULT;
        });
        BlockMatcher.addPreprocessor((expected, found, world, pos) -> expected.getBlock() == Blocks.HOPPER && found.getBlock() == Blocks.HOPPER ? found.setValue(HopperBlock.FACING, expected.getValue(HopperBlock.FACING)) : found);
        BlockMatcher.addPreprocessor((expected, found, world, pos) -> expected.getBlock() == Blocks.CALIBRATED_SCULK_SENSOR && found.getBlock() == Blocks.CALIBRATED_SCULK_SENSOR ? expected : found);
        BlockMatcher.addPreprocessor((expected, found, world, pos) -> {
            if (expected.hasProperty(BlockStateProperties.WATERLOGGED) && found.hasProperty(BlockStateProperties.WATERLOGGED) && !expected.getValue(BlockStateProperties.WATERLOGGED) && found.getValue(BlockStateProperties.WATERLOGGED)) { return found.setValue(BlockStateProperties.WATERLOGGED, false); }
            return found;
        });
    }

    private static List<TagKey<Block>> genericTags() {
        ImmutableList.Builder<TagKey<Block>> tags = ImmutableList.builder();
        for (String metal : METALS) {
            tags.add(tag("c", "storage_blocks/" + metal));
            tags.add(tag("c", "sheetmetals/" + metal));
        }
        tags.add(tag("immersiveengineering", "scaffoldings/aluminum"));
        tags.add(tag("immersiveengineering", "scaffoldings/steel"));
        tags.add(tag("immersiveengineering", "treated_wood_slab"));
        tags.add(tag("immersiveengineering", "treated_wood"));
        tags.add(tag("c", "fences/steel"));
        tags.add(tag("c", "fences/aluminum"));
        return tags.build();
    }

    private static TagKey<Block> tag(String namespace, String path) { return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(namespace, path)); }
}
