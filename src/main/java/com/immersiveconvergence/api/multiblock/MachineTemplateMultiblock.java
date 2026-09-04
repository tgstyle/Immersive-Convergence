package com.immersiveconvergence.api.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.BlockMatcher;
import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "RedundantSuppression"}) public abstract class MachineTemplateMultiblock extends TemplateMultiblock {
    private final MultiblockRegistration<?> logic;
    private final float manualScale;

    public MachineTemplateMultiblock(ResourceLocation loc, BlockPos masterFromOrigin, BlockPos triggerFromOrigin, BlockPos size, float manualScale, MultiblockRegistration<?> logic) {
        super(loc, masterFromOrigin, triggerFromOrigin, size);
        this.manualScale = manualScale;
        this.logic = logic;
    }

    public MachineTemplateMultiblock(ResourceLocation loc, BlockPos masterFromOrigin, BlockPos triggerFromOrigin, BlockPos size, float manualScale, List<BlockMatcher.MatcherPredicate> additionalPredicates, MultiblockRegistration<?> logic) {
        super(loc, masterFromOrigin, triggerFromOrigin, size, additionalPredicates);
        this.manualScale = manualScale;
        this.logic = logic;
    }

    @Override public float getManualScale() { return manualScale; }

    @Override public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer) { consumer.accept(new ClientMultiblockProperties(this)); }

    @Override public boolean canBeMirrored() { return this.logic.mirrorable(); }

    @Override public Component getDisplayName() { return this.logic.block().get().getName(); }

    @Override public Block getBlock() { return this.logic.block().get(); }
}
