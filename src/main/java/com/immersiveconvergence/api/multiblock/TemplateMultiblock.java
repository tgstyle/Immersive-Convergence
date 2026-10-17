package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.block.ModProperties;
import com.immersiveconvergence.api.multiblock.MultiblockHandler.IMultiblock;
import com.immersiveconvergence.api.multiblock.blocks.registry.MultiblockBlockEntityDummy;
import com.immersiveconvergence.api.multiblock.blocks.registry.MultiblockBlockEntityMaster;
import com.immersiveconvergence.api.multiblock.util.DirectionUtils;
import com.immersiveconvergence.core.lib.ICLib;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraftforge.data.loading.DatagenModLoader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import static net.minecraft.world.level.block.Mirror.FRONT_BACK;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection") public abstract class TemplateMultiblock implements IMultiblock {
    public static final Map<ResourceLocation, StructureTemplate> SYNCED_CLIENT_TEMPLATES = new HashMap<>();
    private final ResourceLocation loc;
    protected final BlockPos masterFromOrigin;
    protected final BlockPos triggerFromOrigin;
    protected final BlockPos size;
    protected final List<BlockMatcher.MatcherPredicate> additionalPredicates;
    @Nullable private TemplateData template;
    private List<StructureBlockInfo> sortedStructureBlocks;
    private Map<BlockPos, BlockState> triggerStateMap;
    private int cacheGeneration;

    public record TriggerPoint(BlockPos cell, Rotation offset) {}

    public record TemplateData(StructureTemplate template, List<StructureBlockInfo> blocksWithoutAir, BlockState triggerState) {}

    public TemplateMultiblock(ResourceLocation loc, BlockPos masterFromOrigin, BlockPos triggerFromOrigin, BlockPos size) { this(loc, masterFromOrigin, triggerFromOrigin, size, List.of()); }

    public TemplateMultiblock(ResourceLocation loc, BlockPos masterFromOrigin, BlockPos triggerFromOrigin, BlockPos size, List<BlockMatcher.MatcherPredicate> additionalPredicates) {
        this.loc = loc;
        this.masterFromOrigin = masterFromOrigin;
        this.triggerFromOrigin = triggerFromOrigin;
        this.size = size;
        this.additionalPredicates = additionalPredicates;
    }

    public ResourceLocation getTemplateLocation() { return loc; }

    @Override public ResourceLocation getUniqueName() { return loc; }

    @Override public BlockPos getTriggerOffset() { return triggerFromOrigin; }

    public BlockPos getMasterFromOriginOffset() { return masterFromOrigin; }

    public boolean canBeMirrored() { return true; }

    @Override public List<StructureBlockInfo> getStructure(@Nonnull Level world) { return getTemplate(world).blocksWithoutAir(); }

    public static BlockPos withSettingsAndOffset(BlockPos origin, BlockPos relative, Mirror mirror, Rotation rot) { return origin.offset(getAbsoluteOffset(relative, mirror, rot)); }

    public static BlockPos getAbsoluteOffset(BlockPos relative, Mirror mirror, Rotation rot) { return StructureTemplate.calculateRelativePosition(new StructurePlaceSettings().setMirror(mirror).setRotation(rot), relative); }

    public static BlockPos getAbsoluteOffset(BlockPos relative, boolean mirrored, Direction facing) {
        Rotation rot = DirectionUtils.getRotationBetweenFacings(Direction.NORTH, facing);
        if (rot == null) { return BlockPos.ZERO; }
        return getAbsoluteOffset(relative, mirrored ? FRONT_BACK : Mirror.NONE, rot);
    }

    private void ensureStructureInitialized(@Nonnull Level level) {
        final StructureTemplate newTemplate;
        if (level instanceof ServerLevel serverLevel) { newTemplate = serverLevel.getStructureManager().get(loc).orElse(null); }
        else {
            if (!DatagenModLoader.isRunningDataGen() && !level.isClientSide) { throw new RuntimeException("Unexpected level parameter: " + level); }
            newTemplate = SYNCED_CLIENT_TEMPLATES.get(loc);
        }
        if (newTemplate == null) { throw new RuntimeException("Template " + loc + " does not exist!"); }
        if (this.template != null && newTemplate == template.template()) { return; }
        List<StructureBlockInfo> blocksWithoutAir = new ArrayList<>(newTemplate.palettes.get(0).blocks());
        final Iterator<StructureBlockInfo> it = blocksWithoutAir.iterator();
        BlockState trigger = null;
        while (it.hasNext()) {
            StructureBlockInfo info = it.next();
            if (info.pos().equals(triggerFromOrigin)) { trigger = info.state(); }
            if (info.state() == Blocks.AIR.defaultBlockState()) { it.remove(); }
            else if (info.state().isAir()) { ICLib.IC_LOGGER.error("Found non-default air block in template {}", loc); }
        }
        Preconditions.checkState(trigger != null, "Trigger state was not found in template for " + loc);
        this.template = new TemplateData(newTemplate, blocksWithoutAir, trigger);
    }

    protected List<TriggerPoint> getTriggerPoints() { return List.of(new TriggerPoint(getTriggerOffset(), Rotation.NONE)); }

    protected List<Mirror> getMirrorsToTry() { return canBeMirrored() ? List.of(Mirror.NONE, FRONT_BACK) : List.of(Mirror.NONE); }

    private void ensureCaches(TemplateData data) {
        int generation = MultiblockDataLoader.generation();
        if (sortedStructureBlocks != null && cacheGeneration == generation) { return; }
        cacheGeneration = generation;
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

    protected void replaceStructureBlock(StructureBlockInfo info, Level world, BlockPos actualPos, boolean mirrored, Direction clickDirection, Vec3i offsetFromMaster) {
        BlockState newState = getBlock().defaultBlockState();
        newState = newState.setValue(ModProperties.MULTIBLOCKSLAVE, !offsetFromMaster.equals(Vec3i.ZERO));
        if (newState.hasProperty(ModProperties.MIRRORED)) { newState = newState.setValue(ModProperties.MIRRORED, mirrored); }
        if (newState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) { newState = newState.setValue(BlockStateProperties.HORIZONTAL_FACING, clickDirection.getOpposite()); }
        if (newState.hasProperty(ModProperties.ACTIVE)) { newState = newState.setValue(ModProperties.ACTIVE, false); }
        world.setBlock(actualPos, newState, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
        BlockEntity curr = world.getBlockEntity(actualPos);
        if (curr instanceof MultiblockBlockEntityDummy<?> dummy) { dummy.getHelper().setPositionInMB(info.pos()); }
        else if (!(curr instanceof MultiblockBlockEntityMaster)) { ICLib.IC_LOGGER.error("Expected MB TE at {} during placement", actualPos); }
    }

    @Override public void disassemble(Level world, BlockPos origin, boolean mirrored, Direction clickDirectionAtCreation) {
        if (!(world instanceof ServerLevel serverLevel)) { return; }
        Mirror mirror = mirrored ? FRONT_BACK : Mirror.NONE;
        Rotation rot = DirectionUtils.getRotationBetweenFacings(Direction.NORTH, clickDirectionAtCreation);
        Preconditions.checkNotNull(rot);
        getTemplate(world);
        QueueProcessor.disassemble(serverLevel, sortedStructureBlocks, origin, mirror, rot, withSettingsAndOffset(origin, masterFromOrigin, mirror, rot), size, getBlock(), true);
    }

    @SuppressWarnings("deprecation") private static BlockState rotate(BlockState state, Rotation rotation) { return state.rotate(rotation); }

    @Override public boolean isBlockTrigger(BlockState state, Direction d, @Nonnull Level world) {
        BlockState defaultTrigger = getTemplate(world).triggerState();
        Rotation baseRot = DirectionUtils.getRotationBetweenFacings(Direction.NORTH, d.getOpposite());
        if (baseRot == null) { return false; }
        for (TriggerPoint trigger : getTriggerPoints()) {
            BlockState baseTrigger = triggerStateMap.getOrDefault(trigger.cell(), defaultTrigger);
            Rotation rot = baseRot.getRotated(trigger.offset());
            for (Mirror triedMirror : getMirrorsToTry()) {
                BlockState expected = rotate(baseTrigger.mirror(triedMirror), rot);
                if (BlockMatcher.matches(expected, state, null, null, additionalPredicates).isAllow()) { return true; }
            }
        }
        return false;
    }

    @Override public boolean createStructure(Level world, BlockPos pos, Direction side, Player player) {
        Rotation baseRot = DirectionUtils.getRotationBetweenFacings(Direction.NORTH, side.getOpposite());
        if (baseRot == null) { return false; }
        getTemplate(world);
        List<StructureBlockInfo> structure = getStructure(world);
        for (TriggerPoint trigger : getTriggerPoints()) {
            Rotation rot = baseRot.getRotated(trigger.offset());
            List<FormationCandidate> candidates = new ArrayList<>();
            for (Mirror triedMirror : getMirrorsToTry()) {
                FormationCandidate candidate = FormationCandidate.atTrigger(pos, trigger.cell(), rot, triedMirror);
                if (matches(world, structure, trigger.cell(), candidate)) { candidates.add(candidate); }
            }
            if (candidates.isEmpty()) { continue; }
            FormationCandidate chosen = candidates.size() == 1 ? candidates.get(0) : chooseCandidate(world, candidates, player);
            if (!world.isClientSide) { form(world, chosen.origin(), rot, chosen.mirror(), chosen.front().getOpposite()); }
            return true;
        }
        return false;
    }

    private boolean matches(Level world, List<StructureBlockInfo> structure, BlockPos triggerCell, FormationCandidate candidate) {
        StructurePlaceSettings settings = candidate.settings();
        for (StructureBlockInfo info : structure) {
            if (info.pos().equals(triggerCell)) { continue; }
            BlockPos here = candidate.origin().offset(StructureTemplate.calculateRelativePosition(settings, info.pos()));
            BlockState expected = rotate(info.state().mirror(candidate.mirror()), candidate.rotation());
            if (!BlockMatcher.matches(expected, world.getBlockState(here), world, here, additionalPredicates).isAllow()) { return false; }
        }
        return true;
    }

    private FormationCandidate chooseCandidate(Level world, List<FormationCandidate> candidates, @Nullable Player player) {
        if (player != null && player.isShiftKeyDown()) {
            for (FormationCandidate candidate : candidates) { if (candidate.mirrored()) { return candidate; } }
        }
        FormationCandidate preferred = preferredCandidate(world, candidates);
        return preferred != null ? preferred : candidates.get(0);
    }

    @Nullable protected FormationCandidate preferredCandidate(Level world, List<FormationCandidate> candidates) { return null; }

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
            for (Direction dir : DirectionUtils.VALUES) {
                BlockPos neighborPos = placedPos.relative(dir);
                if (!placedPositions.contains(neighborPos)) { world.neighborChanged(neighborPos, block, placedPos); }
            }
        }
    }

    @Override public Vec3i getSize(@Nullable Level world) { return this.size; }

    @Nonnull public TemplateData getTemplate(@Nonnull Level world) {
        ensureStructureInitialized(world);
        TemplateData result = Objects.requireNonNull(template);
        Vec3i resultSize = result.template().getSize();
        Preconditions.checkState(resultSize.equals(this.size), "Wrong template size for multiblock %s, template size: %s", loc, resultSize);
        ensureCaches(result);
        return result;
    }
}
