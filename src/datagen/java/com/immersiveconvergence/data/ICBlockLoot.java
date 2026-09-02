package com.immersiveconvergence.data;

import com.immersiveconvergence.core.registration.ICBlocks;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;
import java.util.Collections;

public class ICBlockLoot extends BlockLootSubProvider {
    public ICBlockLoot(HolderLookup.Provider lookup) { super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags(), lookup); }

    @Override protected void generate() { getKnownBlocks().forEach(this::dropSelf); }

    @Override @Nonnull
    protected Iterable<Block> getKnownBlocks() { return () -> ICBlocks.ICBlockEntry.ALL_ENTRIES.stream().map(entry -> (Block)entry.get()).iterator(); }
}
