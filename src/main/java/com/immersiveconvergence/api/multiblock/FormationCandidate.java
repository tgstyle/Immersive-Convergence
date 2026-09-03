package com.immersiveconvergence.api.multiblock;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.BiPredicate;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public final class FormationCandidate {
    private final TemplateMultiblock template;
    public final BlockPos origin;
    public final EnumFacing side;
    public final boolean mirrored;

    FormationCandidate(TemplateMultiblock template, BlockPos origin, EnumFacing side, boolean mirrored) {
        this.template = template;
        this.origin = origin;
        this.side = side;
        this.mirrored = mirrored;
    }

    public BlockPos toWorld(BlockPos posInMultiblock) { return TemplateMultiblock.localToWorld(origin, template.localX(posInMultiblock.getX(), mirrored), posInMultiblock.getY(), posInMultiblock.getZ(), side); }

    public EnumFacing toWorld(LocalFacing facing) {
        EnumFacing global = facing.LocalToGlobal(side);
        return mirrored && (facing == LocalFacing.LEFT || facing == LocalFacing.RIGHT) ? global.getOpposite() : global;
    }

    public boolean faces(World world, PoIJSONSchema[] pointsOfInterest, String poiName, BiPredicate<TileEntity, EnumFacing> partner) {
        for (PoIJSONSchema poi : pointsOfInterest) {
            if (!poiName.equals(poi.name) || poi.localFacing == null) { continue; }
            EnumFacing direction = toWorld(poi.localFacing);
            TileEntity neighbor = world.getTileEntity(toWorld(poi.position).offset(direction));
            if (neighbor != null && partner.test(neighbor, direction.getOpposite())) { return true; }
        }
        return false;
    }

    @Nullable public static FormationCandidate preferFacing(World world, List<FormationCandidate> candidates, PoIJSONSchema[] pointsOfInterest, String poiName, BiPredicate<TileEntity, EnumFacing> partner) {
        FormationCandidate found = null;
        for (FormationCandidate candidate : candidates) {
            if (!candidate.faces(world, pointsOfInterest, poiName, partner)) { continue; }
            if (found != null) { return null; }
            found = candidate;
        }
        return found;
    }

    @Override public String toString() { return "FormationCandidate[origin=" + origin + ", side=" + side + ", mirrored=" + mirrored + "]"; }
}
