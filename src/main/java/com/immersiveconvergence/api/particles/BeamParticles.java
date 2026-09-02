package com.immersiveconvergence.api.particles;

import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unused") public final class BeamParticles {
    private static final float SPAWN_CHANCE = 0.04f;
    private static final double BEAM_FRACTION = 0.9;
    private static final double SPEED_MIN = 0.08;
    private static final double SPEED_VARIANCE = 0.05;
    private static final double SPREAD = 0.005;
    private static final double MAX_DISTANCE = 64;

    private BeamParticles() {}

    public static void spawnAlongBeam(World world, Vec3d start, Vec3d end, Random random) {
        if (random.nextFloat() >= SPAWN_CHANCE) { return; }
        Vec3d diff = end.subtract(start);
        double distance = diff.length();
        if (distance > MAX_DISTANCE) { return; }
        Vec3d direction = diff.normalize();
        Vec3d pos = start.add(direction.scale(random.nextDouble() * distance * BEAM_FRACTION));
        Vec3d perpendicular = direction.crossProduct(new Vec3d(0, 1, 0)).normalize().scale(random.nextGaussian() * SPREAD);
        Vec3d velocity = direction.scale(SPEED_MIN + random.nextDouble() * SPEED_VARIANCE)
                .add(perpendicular)
                .add(direction.crossProduct(perpendicular).normalize().scale(random.nextGaussian() * SPREAD));
        world.spawnParticle(EnumParticleTypes.END_ROD, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z);
    }
}
