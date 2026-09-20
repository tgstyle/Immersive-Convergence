package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.common.util.ICLogger;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import javax.annotation.Nullable;
import java.util.Arrays;

public final class MultiblockFormationTrace {
    private MultiblockFormationTrace() {}

    @Nullable public static TileEntity retryPart(World world, BlockPos worldPos, int position, @Nullable TileEntity tile, Class<?> partType) {
        ICLogger.error("IC multiblock formation: no part at " + worldPos + " (position " + position + "): found " + describe(tile) + " instead of " + partType.getSimpleName()
                + ", state " + world.getBlockState(worldPos) + ", thread " + Thread.currentThread().getName() + ", remote " + world.isRemote);
        TileEntity second = world.getTileEntity(worldPos);
        ICLogger.error("IC multiblock formation: second read at " + worldPos + " gave " + describe(second) + ", part " + partType.isInstance(second));
        return partType.isInstance(second) ? second : null;
    }

    public static void verifyWritten(World world, BlockPos worldPos, BlockPos masterWorldPos, int position, TileEntity written) {
        TileEntity current = world.getTileEntity(worldPos);
        if (current != written) { ICLogger.error("IC multiblock formation: instance changed at " + worldPos + " (position " + position + "): wrote " + describe(written) + ", world now holds " + describe(current)); }
        ICMultiblockPart part = ICMultiblockPart.of(current);
        if (part == null) {
            ICLogger.error("IC multiblock formation: readback at " + worldPos + " (position " + position + ") is not a part: " + describe(current));
            return;
        }
        int[] expected = {worldPos.getX() - masterWorldPos.getX(), worldPos.getY() - masterWorldPos.getY(), worldPos.getZ() - masterWorldPos.getZ()};
        if (part.isPartUnformed() || part.getPartPos() != position || !Arrays.equals(part.getPartOffset(), expected)) {
            ICLogger.error("IC multiblock formation: readback at " + worldPos + " holds formed " + !part.isPartUnformed() + ", pos " + part.getPartPos() + ", offset " + Arrays.toString(part.getPartOffset())
                    + " after writing formed true, pos " + position + ", offset " + Arrays.toString(expected));
        }
    }

    private static String describe(@Nullable TileEntity tile) { return tile == null ? "no tile entity" : tile.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(tile)); }
}
