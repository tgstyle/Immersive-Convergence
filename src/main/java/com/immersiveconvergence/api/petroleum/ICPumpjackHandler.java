package com.immersiveconvergence.api.petroleum;

import com.immersiveconvergence.core.ICCommonConfig;

import blusunrize.immersiveengineering.api.DimensionChunkCoords;
import flaxbeard.immersivepetroleum.api.crafting.PumpjackHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public final class ICPumpjackHandler {
    private ICPumpjackHandler() {}

    @Nullable public static PumpjackHandler.ReservoirType reservoirUnder(World world, BlockPos pos) {
        PumpjackHandler.OilWorldInfo info = PumpjackHandler.getOilWorldInfo(world, pos.getX() >> 4, pos.getZ() >> 4);
        return info == null ? null : info.getType();
    }

    @Nullable public static ICReservoirData dataUnder(World world, BlockPos pos) { return ICReservoirHolder.of(reservoirUnder(world, pos)); }

    public static ICPowerTier powerTierUnder(World world, BlockPos pos) {
        ICReservoirData data = dataUnder(world, pos);
        return data == null ? ICPowerTiers.fallback() : ICPowerTiers.get(data.powerTier);
    }

    public static ICReservoirContent contentUnder(World world, BlockPos pos) {
        ICReservoirData data = dataUnder(world, pos);
        return data == null ? ICReservoirContent.EMPTY : data.content;
    }

    public static int pumpSpeedUnder(World world, BlockPos pos) { return pumpSpeedOf(dataUnder(world, pos)); }

    public static int pumpSpeedOf(@Nullable ICReservoirData data) {
        int declared = data == null ? 0 : data.pumpSpeed;
        return declared > 0 ? declared : ICCommonConfig.petroleum.defaultPumpSpeed;
    }

    public static int claimReplenish(World world, BlockPos pos) {
        PumpjackHandler.ReservoirType reservoir = reservoirUnder(world, pos);
        if (reservoir == null || reservoir.replenishRate <= 0) { return 0; }
        return claimThisTick(world, pos) ? reservoir.replenishRate : 0;
    }

    private static boolean claimThisTick(World world, BlockPos pos) {
        DimensionChunkCoords deposit = new DimensionChunkCoords(world.provider.getDimension(), pos.getX() >> 4, pos.getZ() >> 4);
        long now = world.getTotalWorldTime();
        Long claimed = PumpjackHandler.timeCache.put(deposit, now);
        return claimed == null || claimed != now;
    }

    public static ICReservoirData register(String name, String fluid, int minSize, int maxSize, int replenishRate, int weight, int pumpSpeed, int powerTier) {
        PumpjackHandler.ReservoirType type = new PumpjackHandler.ReservoirType(name, fluid, minSize, maxSize, replenishRate);
        PumpjackHandler.reservoirList.put(type, weight);
        ICReservoirData data = ICReservoirHolder.of(type);
        if (data != null) {
            data.pumpSpeed = pumpSpeed;
            data.powerTier = powerTier;
        }
        return data;
    }
}
