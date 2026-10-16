package com.immersiveconvergence.api.multiblock.blocks.util;

import com.immersiveconvergence.api.block.ModProperties;
import com.immersiveconvergence.api.multiblock.TemplateMultiblock;
import com.immersiveconvergence.api.multiblock.util.DirectionUtils;
import com.immersiveconvergence.api.multiblock.util.MultiblockShapeUtils;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.Objects;

public record MultiblockOrientation(Direction front, boolean mirrored) {
    public MultiblockOrientation { Preconditions.checkArgument(front.getAxis().isHorizontal()); }

    public MultiblockOrientation(BlockState blockState, boolean mirrorable) {
        this(
                blockState.getValue(ModProperties.FACING_HORIZONTAL),
                mirrorable && blockState.getValue(ModProperties.MIRRORED)
        );
    }

    public BlockPos getAbsoluteOffset(BlockPos offsetInMB) { return TemplateMultiblock.getAbsoluteOffset(offsetInMB, mirrored, front); }

    public Vec3 getAbsoluteOffset(Vec3 relative) {
        Rotation rot = DirectionUtils.getRotationBetweenFacings(Direction.NORTH, front);
        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setMirror(mirrored ? Mirror.FRONT_BACK : Mirror.NONE)
                .setRotation(Objects.requireNonNull(rot));
        return StructureTemplate.transformedVec3d(settings, relative);
    }

    public VoxelShape transformRelativeShape(VoxelShape relative) {
        VoxelShape ret = Shapes.empty();
        for (AABB aabb : relative.toAabbs()) {
            final AABB newBox = MultiblockShapeUtils.withFacingAndMirror(aabb, front, mirrored);
            ret = Shapes.joinUnoptimized(ret, Shapes.create(newBox), BooleanOp.OR);
        }
        return ret.optimize();
    }
}
