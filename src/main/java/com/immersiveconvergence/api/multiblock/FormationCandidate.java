package com.immersiveconvergence.api.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MultiblockOrientation;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.neoforge.capabilities.BlockCapability;

import java.util.List;

import javax.annotation.Nullable;

@SuppressWarnings({"unused", "RedundantSuppression"}) public record FormationCandidate(BlockPos origin, Rotation rotation, Mirror mirror) {
    public static FormationCandidate atTrigger(BlockPos triggerWorldPos, BlockPos triggerCell, Rotation rotation, Mirror mirror) {
        StructurePlaceSettings settings = new StructurePlaceSettings().setMirror(mirror).setRotation(rotation);
        return new FormationCandidate(triggerWorldPos.subtract(StructureTemplate.calculateRelativePosition(settings, triggerCell)), rotation, mirror);
    }

    public boolean mirrored() { return mirror != Mirror.NONE; }

    public Direction front() { return rotation.rotate(Direction.NORTH); }

    public MultiblockOrientation orientation() { return new MultiblockOrientation(front(), mirrored()); }

    public StructurePlaceSettings settings() { return new StructurePlaceSettings().setMirror(mirror).setRotation(rotation); }

    public BlockPos toAbsolute(BlockPos posInMultiblock) { return origin.offset(StructureTemplate.calculateRelativePosition(settings(), posInMultiblock)); }

    public Direction toAbsolute(RelativeBlockFace face) { return face.forFront(orientation()); }

    public boolean faces(Level level, List<BlockPos> portCells, RelativeBlockFace portFace, BlockCapability<?, Direction> partnerCapability) {
        Direction direction = toAbsolute(portFace);
        for (BlockPos cell : portCells) {
            if (level.getCapability(partnerCapability, toAbsolute(cell).relative(direction), direction.getOpposite()) != null) { return true; }
        }
        return false;
    }

    @Nullable public static FormationCandidate preferFacing(Level level, List<FormationCandidate> candidates, List<BlockPos> portCells, RelativeBlockFace portFace, BlockCapability<?, Direction> partnerCapability) {
        FormationCandidate found = null;
        for (FormationCandidate candidate : candidates) {
            if (!candidate.faces(level, portCells, portFace, partnerCapability)) { continue; }
            if (found != null) { return null; }
            found = candidate;
        }
        return found;
    }
}
