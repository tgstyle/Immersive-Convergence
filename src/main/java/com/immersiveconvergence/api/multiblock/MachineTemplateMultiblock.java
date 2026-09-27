package com.immersiveconvergence.api.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.BlockMatcher;
import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "RedundantSuppression"}) public abstract class MachineTemplateMultiblock extends TemplateMultiblock {
    private final MultiblockRegistration<?> logic;
    private final float manualScale;
    @Nullable private Supplier<ShapeData> shape;

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

    public MachineTemplateMultiblock(ResourceLocation loc, Supplier<ShapeData> shape, MultiblockRegistration<?> logic) {
        this(loc, shape.get().masterPos, shape.get().triggerPos, sizeOf(shape.get()), shape.get().manualScale, logic);
        this.shape = shape;
    }

    public MachineTemplateMultiblock(ResourceLocation loc, Supplier<ShapeData> shape, List<BlockMatcher.MatcherPredicate> additionalPredicates, MultiblockRegistration<?> logic) {
        this(loc, shape.get().masterPos, shape.get().triggerPos, sizeOf(shape.get()), shape.get().manualScale, additionalPredicates, logic);
        this.shape = shape;
    }

    private static BlockPos sizeOf(ShapeData shape) { return new BlockPos(shape.width, shape.height, shape.length); }

    @Override public float getManualScale() { return shape == null ? manualScale : shape.get().manualScale; }

    @Override public BlockPos getTriggerOffset() {
        BlockPos trigger = shape == null ? null : shape.get().triggerPos;
        return trigger == null ? super.getTriggerOffset() : trigger;
    }

    @Override public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer) { consumer.accept(new ClientMultiblockProperties(this)); }

    @Override public boolean canBeMirrored() { return this.logic.mirrorable(); }

    @Override public Component getDisplayName() { return this.logic.block().get().getName(); }

    @Override public Block getBlock() { return this.logic.block().get(); }
}
