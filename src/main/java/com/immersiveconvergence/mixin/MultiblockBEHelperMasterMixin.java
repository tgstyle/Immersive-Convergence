package com.immersiveconvergence.mixin;

import com.immersiveconvergence.api.multiblock.IDataReloadAware;
import com.immersiveconvergence.api.multiblock.MultiblockDataLoader;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockLevel;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MultiblockOrientation;
import blusunrize.immersiveengineering.api.utils.SafeChunkUtils;
import blusunrize.immersiveengineering.common.blocks.multiblocks.blockimpl.InitialMultiblockContext;
import blusunrize.immersiveengineering.common.blocks.multiblocks.blockimpl.MultiblockBEHelperMaster;
import blusunrize.immersiveengineering.common.blocks.multiblocks.blockimpl.MultiblockContext;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

@Mixin(value = MultiblockBEHelperMaster.class, remap = false) public abstract class MultiblockBEHelperMasterMixin {
    @Shadow(remap = false) @Final private IMultiblockState state;
    @Shadow(remap = false) @Final private MultiblockContext<?> context;
    @Shadow(remap = false) @Final private Object2IntMap<BlockPos> currentComparatorOutputs;
    @Unique private int ic$dataGeneration = MultiblockDataLoader.generation();

    @Shadow(remap = false) public abstract BlockEntity getMasterBE();

    @Shadow(remap = false) public abstract MultiblockOrientation getOrientation();

    @Shadow(remap = false) public abstract MultiblockRegistration<?> getMultiblock();

    @Shadow(remap = false) public abstract void invalidateCaps();

    @Inject(method = "tickServer", at = @At("HEAD"), remap = false)
    private void ic$followDataReload(CallbackInfo ci) {
        int generation = MultiblockDataLoader.generation();
        if (ic$dataGeneration == generation) { return; }
        BlockEntity be = getMasterBE();
        Level level = be.getLevel();
        if (level == null || !SafeChunkUtils.isChunkSafe(level, be.getBlockPos())) { return; }
        ic$dataGeneration = generation;
        invalidateCaps();
        for (BlockPos pos : List.copyOf(currentComparatorOutputs.keySet())) { context.setComparatorOutputFor(pos, 0); }
        if (state instanceof IDataReloadAware aware) { aware.onDataReload(new InitialMultiblockContext<>(be, getOrientation(), getMultiblock().masterPosInMB())); }
        IMultiblockLevel mbLevel = context.getLevel();
        for (StructureTemplate.StructureBlockInfo info : getMultiblock().getStructure().apply(level)) {
            BlockPos pos = mbLevel.toAbsolute(info.pos());
            if (ic$neighborhoodSafe(level, pos)) {
                BlockState cell = level.getBlockState(pos);
                if (!cell.isAir()) { level.updateNeighborsAt(pos, cell.getBlock()); }
            }
        }
    }

    @Unique private static boolean ic$neighborhoodSafe(Level level, BlockPos pos) {
        if (!SafeChunkUtils.isChunkSafe(level, pos)) { return false; }
        for (Direction dir : Direction.values()) {
            if (!SafeChunkUtils.isChunkSafe(level, pos.relative(dir))) { return false; }
        }
        return true;
    }
}
