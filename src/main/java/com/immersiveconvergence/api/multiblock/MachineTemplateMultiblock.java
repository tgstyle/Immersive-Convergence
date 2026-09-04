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
    private MultiblockRegistration<?> multiblockRegistration;
    private final float manualScale;

    public MachineTemplateMultiblock(ResourceLocation loc, BlockPos masterFromOrigin, BlockPos triggerFromOrigin, BlockPos size, float manualScale, MultiblockRegistration<?> logic) {
        super(loc, masterFromOrigin, triggerFromOrigin, size);
        this.manualScale = manualScale;
        this.multiblockRegistration = logic;
    }

    public MachineTemplateMultiblock(ResourceLocation loc, BlockPos masterFromOrigin, BlockPos triggerFromOrigin, BlockPos size, float manualScale, List<BlockMatcher.MatcherPredicate> additionalPredicates, MultiblockRegistration<?> logic) {
        super(loc, masterFromOrigin, triggerFromOrigin, size, additionalPredicates);
        this.manualScale = manualScale;
        this.multiblockRegistration = logic;
    }

    public MachineTemplateMultiblock(ResourceLocation loc, BlockPos masterFromOrigin, BlockPos triggerFromOrigin, BlockPos size, float manualScale) {
        super(loc, masterFromOrigin, triggerFromOrigin, size);
        this.manualScale = manualScale;
    }

    public MachineTemplateMultiblock(ResourceLocation loc, BlockPos masterFromOrigin, BlockPos triggerFromOrigin, BlockPos size, float manualScale, List<BlockMatcher.MatcherPredicate> additionalPredicates) {
        super(loc, masterFromOrigin, triggerFromOrigin, size, additionalPredicates);
        this.manualScale = manualScale;
    }

    public void setLogic(MultiblockRegistration<?> logic) { this.multiblockRegistration = logic; }

    @Override public float getManualScale() { return manualScale; }

    @Override public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer) { consumer.accept(new ClientMultiblockProperties(this)); }

    @Override public boolean canBeMirrored() {
        if (multiblockRegistration != null) { return multiblockRegistration.mirrorable(); }
        return super.canBeMirrored();
    }

    @Override public Component getDisplayName() { return this.getBlock().getName(); }

    @Override public Block getBlock() {
        if (multiblockRegistration != null && multiblockRegistration.block() != null) { return multiblockRegistration.block().get(); }
        return super.getBlock();
    }
}
