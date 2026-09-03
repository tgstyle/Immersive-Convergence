package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.core.lib.ICLib;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.BlockMatcher;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityDummy;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.api.utils.DirectionUtils;
import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

import java.util.*;

import static net.minecraft.world.level.block.Mirror.FRONT_BACK;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings({"unused", "RedundantSuppression", "rawtypes", "unchecked", "MismatchedQueryAndUpdateOfCollection"}) public abstract class TemplateMultiblock extends blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock {
    private List<StructureBlockInfo> sortedStructureBlocks;
    private Map<BlockPos, BlockState> triggerStateMap;

    public record TriggerPoint(BlockPos cell, Rotation offset) {}

    public TemplateMultiblock(ResourceLocation loc, BlockPos masterFromOrigin, BlockPos triggerFromOrigin, BlockPos size) {
        super(loc, masterFromOrigin, triggerFromOrigin, size);
    }

    public TemplateMultiblock(ResourceLocation loc, BlockPos masterFromOrigin, BlockPos triggerFromOrigin, BlockPos size, List<BlockMatcher.MatcherPredicate> additionalPredicates) {
        super(loc, masterFromOrigin, triggerFromOrigin, size, additionalPredicates);
    }

    protected List<TriggerPoint> getTriggerPoints() { return List.of(new TriggerPoint(this.triggerFromOrigin, Rotation.NONE)); }

    protected List<Mirror> getMirrorsToTry() { return canBeMirrored() ? List.of(Mirror.NONE, FRONT_BACK) : List.of(Mirror.NONE); }

    private void ensureCaches(blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock.TemplateData data) {
        if (sortedStructureBlocks != null) { return; }
        List<StructureBlockInfo> nonAir = data.blocksWithoutAir();
        sortedStructureBlocks = new ArrayList<>(data.template().palettes.get(0).blocks());
        sortedStructureBlocks.sort(Comparator.comparingInt(info -> -info.pos().getY()));
        triggerStateMap = new HashMap<>();
        for (TriggerPoint trigger : getTriggerPoints()) {
            for (StructureBlockInfo info : nonAir) {
                if (info.pos().equals(trigger.cell())) {
                    triggerStateMap.put(trigger.cell(), info.state());
                    break;
                }
            }
        }
    }

    @Override protected void replaceStructureBlock(StructureTemplate.StructureBlockInfo info, Level world, BlockPos actualPos, boolean mirrored, Direction clickDirection, Vec3i offsetFromMaster) {
        BlockState newState = getBlock().defaultBlockState();
        newState = newState.setValue(IEProperties.MULTIBLOCKSLAVE, !offsetFromMaster.equals(Vec3i.ZERO));
        if (newState.hasProperty(IEProperties.MIRRORED)) { newState = newState.setValue(IEProperties.MIRRORED, mirrored); }
        if (newState.hasProperty(IEProperties.FACING_HORIZONTAL)) { newState = newState.setValue(IEProperties.FACING_HORIZONTAL, clickDirection.getOpposite()); }
        if (newState.hasProperty(IEProperties.ACTIVE)) { newState = newState.setValue(IEProperties.ACTIVE, false); }
        world.setBlock(actualPos, newState, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
        BlockEntity curr = world.getBlockEntity(actualPos);
        if (curr instanceof MultiblockBlockEntityDummy<?> dummy) { dummy.getHelper().setPositionInMB(info.pos()); }
        else if (!(curr instanceof MultiblockBlockEntityMaster)) { ICLib.IC_LOGGER.error("Expected MB TE at {} during placement", actualPos); }
    }

    @SuppressWarnings("deprecation")
    @Override public void disassemble(Level world, BlockPos origin, boolean mirrored, Direction clickDirectionAtCreation) {
        if (!(world instanceof ServerLevel serverLevel)) { return; }
        Mirror mirror = mirrored ? FRONT_BACK : Mirror.NONE;
        Rotation rot = DirectionUtils.getRotationBetweenFacings(Direction.NORTH, clickDirectionAtCreation);
        Preconditions.checkNotNull(rot);
        getTemplate(world);
        QueueProcessor.disassemble(serverLevel, sortedStructureBlocks, origin, mirror, rot, withSettingsAndOffset(origin, masterFromOrigin, mirror, rot), true);
    }

    @SuppressWarnings("deprecation")
    @Override public boolean isBlockTrigger(BlockState state, Direction d, @Nonnull Level world) {
        BlockState defaultTrigger = getTemplate(world).triggerState();
        Rotation baseRot = DirectionUtils.getRotationBetweenFacings(Direction.NORTH, d.getOpposite());
        if (baseRot == null) { return false; }
        for (TriggerPoint trigger : getTriggerPoints()) {
            BlockState baseTrigger = triggerStateMap.getOrDefault(trigger.cell(), defaultTrigger);
            Rotation rot = baseRot.getRotated(trigger.offset());
            for (Mirror triedMirror : getMirrorsToTry()) {
                BlockState expected = baseTrigger.mirror(triedMirror).rotate(rot);
                if (BlockMatcher.matches(expected, state, null, null, additionalPredicates).isAllow()) { return true; }
            }
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override public boolean createStructure(Level world, BlockPos pos, Direction side, net.minecraft.world.entity.player.Player player) {
        Rotation baseRot = DirectionUtils.getRotationBetweenFacings(Direction.NORTH, side.getOpposite());
        if (baseRot == null) { return false; }
        getTemplate(world);
        List<StructureTemplate.StructureBlockInfo> structure = getStructure(world);
        for (TriggerPoint trigger : getTriggerPoints()) {
            Rotation rot = baseRot.getRotated(trigger.offset());
            for (Mirror triedMirror : getMirrorsToTry()) {
                StructurePlaceSettings placeSettings = new StructurePlaceSettings().setMirror(triedMirror).setRotation(rot);
                BlockPos origin = pos.subtract(StructureTemplate.calculateRelativePosition(placeSettings, trigger.cell()));
                boolean allMatch = true;
                for (StructureBlockInfo info : structure) {
                    if (info.pos().equals(trigger.cell())) { continue; }
                    BlockPos here = origin.offset(StructureTemplate.calculateRelativePosition(placeSettings, info.pos()));
                    BlockState expected = info.state().mirror(triedMirror).rotate(rot);
                    BlockState inWorld = world.getBlockState(here);
                    if (!BlockMatcher.matches(expected, inWorld, world, here, additionalPredicates).isAllow()) {
                        allMatch = false;
                        break;
                    }
                }
                if (allMatch) {
                    Direction formSide = rot.rotate(Direction.NORTH).getOpposite();
                    if (!world.isClientSide) { form(world, origin, rot, triedMirror, formSide); }
                    return true;
                }
            }
        }
        return false;
    }

    protected void form(Level world, BlockPos origin, Rotation rot, Mirror mirrorForSettings, Direction side) {
        StructureTemplate template = getTemplate(world).template();
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(rot).setMirror(mirrorForSettings);
        boolean mirrored = mirrorForSettings != Mirror.NONE;
        Set<BlockPos> placedPositions = new HashSet<>();
        for (StructureBlockInfo info : template.palettes.get(0).blocks()) {
            BlockPos actualPos = origin.offset(StructureTemplate.calculateRelativePosition(settings, info.pos()));
            Vec3i offsetFromMaster = info.pos().subtract(masterFromOrigin);
            replaceStructureBlock(info, world, actualPos, mirrored, side, offsetFromMaster);
            placedPositions.add(actualPos);
        }
        Block block = getBlock();
        for (BlockPos placedPos : placedPositions) {
            for (Direction dir : Direction.values()) {
                BlockPos neighborPos = placedPos.relative(dir);
                if (!placedPositions.contains(neighborPos)) { world.neighborChanged(neighborPos, block, placedPos); }
            }
        }
    }


    public Vec3i getSize(@Nullable Level world) { return this.size; }

    @Nonnull public blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock.TemplateData getTemplate(@Nonnull Level world) {
        blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock.TemplateData result = super.getTemplate(world);
        Vec3i resultSize = result.template().getSize();
        Preconditions.checkState(resultSize.equals(this.size), "Wrong template size for multiblock %s, template size: %s", this.getTemplateLocation(), resultSize);
        ensureCaches(result);
        return result;
    }
}
