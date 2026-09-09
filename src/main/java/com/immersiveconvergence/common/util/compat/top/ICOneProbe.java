package com.immersiveconvergence.common.util.compat.top;

import com.immersiveconvergence.ImmersiveConvergence;
import com.immersiveconvergence.api.ICLib;
import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.petroleum.ICPowerTier;
import com.immersiveconvergence.api.petroleum.ICPumpjackHandler;
import com.immersiveconvergence.api.petroleum.ICReservoirData;
import com.immersiveconvergence.common.util.compat.ICCompatModule;

import flaxbeard.immersivepetroleum.api.crafting.PumpjackHandler;
import flaxbeard.immersivepetroleum.common.blocks.metal.TileEntityPumpjack;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.function.Function;

public class ICOneProbe extends ICCompatModule implements Function<ITheOneProbe, Void> {
    private static final NumberFormat NUMBERS = NumberFormat.getInstance();

    @Override public void preInit() {
        if (ICMods.immersivePetroleum()) { FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", getClass().getName()); }
    }

    @Override @Nullable public Void apply(@Nullable ITheOneProbe probe) {
        if (probe != null) { probe.registerProvider(new ReservoirProvider()); }
        return null;
    }

    private static class ReservoirProvider implements IProbeInfoProvider {
        @Override public String getID() { return ImmersiveConvergence.MODID + ":reservoir"; }

        @Override public void addProbeInfo(ProbeMode mode, IProbeInfo info, EntityPlayer player, World world, IBlockState state, IProbeHitData data) {
            TileEntity tile = world.getTileEntity(data.getPos());
            if (!(tile instanceof TileEntityPumpjack)) { return; }
            PumpjackHandler.ReservoirType reservoir = ICPumpjackHandler.reservoirUnder(world, data.getPos());
            if (reservoir == null) {
                info.text(I18n.format(ICLib.DESC_INFO + "reservoir.none"));
                return;
            }
            PumpjackHandler.OilWorldInfo deposit = PumpjackHandler.getOilWorldInfo(world, data.getPos().getX() >> 4, data.getPos().getZ() >> 4);
            String name = reservoir.name;
            String key = "desc.immersivepetroleum.info.reservoir." + name;
            info.text(I18n.hasKey(key) ? I18n.format(key) : name);
            if (deposit != null) { info.text(I18n.format(ICLib.DESC_INFO + "reservoir.remaining", NUMBERS.format(deposit.current), NUMBERS.format(deposit.capacity))); }
            info.text(I18n.format(ICLib.DESC_INFO + "reservoir.pumpSpeed", NUMBERS.format(ICPumpjackHandler.pumpSpeedUnder(world, data.getPos()))));
            ICPowerTier tier = ICPumpjackHandler.powerTierUnder(world, data.getPos());
            info.text(I18n.format(ICLib.DESC_INFO + "reservoir.powerTier", NUMBERS.format(tier.getUsage()), NUMBERS.format(tier.getCapacity())));
            ICReservoirData extra = ICPumpjackHandler.dataUnder(world, data.getPos());
            if (extra != null && extra.drainChance != 1F) { info.text(I18n.format(ICLib.DESC_INFO + "reservoir.drainChance", Math.round(extra.drainChance * 100))); }
        }
    }
}
