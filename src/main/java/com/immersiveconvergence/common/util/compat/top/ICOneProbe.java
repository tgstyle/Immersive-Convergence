package com.immersiveconvergence.common.util.compat.top;

import com.immersiveconvergence.ImmersiveConvergence;
import com.immersiveconvergence.api.ICLib;
import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.energy.IICFluxProvider;
import com.immersiveconvergence.api.energy.IICFluxAcceptor;
import com.immersiveconvergence.api.block.ICSideConfig;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IConfigurableSides;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IProcessTile;
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
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.function.Function;

public class ICOneProbe extends ICCompatModule implements Function<ITheOneProbe, Void> {
    private static final NumberFormat NUMBERS = NumberFormat.getInstance();

    @Override public void preInit() {
        FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", getClass().getName());
    }

    @Override @Nullable public Void apply(@Nullable ITheOneProbe probe) {
        if (probe == null) { return null; }
        probe.registerProvider(new EnergyProvider());
        probe.registerProvider(new ProcessProvider());
        probe.registerProvider(new SideConfigProvider());
        if (ICMods.immersivePetroleum()) { probe.registerProvider(new ReservoirProvider()); }
        return null;
    }

    private static class EnergyProvider implements IProbeInfoProvider {
        @Override public String getID() { return ImmersiveConvergence.MODID + ":energy"; }

        @Override public void addProbeInfo(ProbeMode mode, IProbeInfo info, EntityPlayer player, World world, IBlockState state, IProbeHitData data) {
            TileEntity tile = world.getTileEntity(data.getPos());
            int stored = 0;
            int max = 0;
            if (tile instanceof IICFluxAcceptor) {
                stored = ((IICFluxAcceptor)tile).getEnergyStored(null);
                max = ((IICFluxAcceptor)tile).getMaxEnergyStored(null);
            }
            else if (tile instanceof IICFluxProvider) {
                stored = ((IICFluxProvider)tile).getEnergyStored(null);
                max = ((IICFluxProvider)tile).getMaxEnergyStored(null);
            }
            if (max <= 0) { return; }
            info.progress(stored, max, info.defaultProgressStyle()
                    .suffix("IF")
                    .filledColor(ICLib.COLOUR_I_ImmersiveOrange)
                    .alternateFilledColor(0xff994f20)
                    .borderColor(ICLib.COLOUR_I_ImmersiveOrangeShadow)
                    .numberFormat(mcjty.theoneprobe.api.NumberFormat.COMPACT));
        }


    }

    private static class ProcessProvider implements IProbeInfoProvider {
        @Override public String getID() { return ImmersiveConvergence.MODID + ":process"; }

        @Override public void addProbeInfo(ProbeMode mode, IProbeInfo info, EntityPlayer player, World world, IBlockState state, IProbeHitData data) {
            TileEntity tile = world.getTileEntity(data.getPos());
            if (!(tile instanceof IProcessTile)) { return; }
            int[] current = ((IProcessTile)tile).getCurrentProcessesStep();
            int[] maximum = ((IProcessTile)tile).getCurrentProcessesMax();
            int slots = Math.min(current.length, maximum.length);
            if (slots < 1) { return; }
            int height = Math.max(4, (int)Math.ceil(12 / (float)slots));
            for (int i = 0; i < slots; i++) {
                float percent = maximum[i] > 0 ? current[i] / (float)maximum[i] * 100 : 0;
                info.progress((int)percent, 100, info.defaultProgressStyle().showText(height >= 10).suffix("%").height(height));
            }
        }


    }

    private static class SideConfigProvider implements IProbeInfoProvider {
        @Override public String getID() { return ImmersiveConvergence.MODID + ":sideconfig"; }

        @Override public void addProbeInfo(ProbeMode mode, IProbeInfo info, EntityPlayer player, World world, IBlockState state, IProbeHitData data) {
            TileEntity tile = world.getTileEntity(data.getPos());
            if (!(tile instanceof IConfigurableSides) || data.getSideHit() == null) { return; }
            boolean flip = player.isSneaking();
            EnumFacing side = flip ? data.getSideHit().getOpposite() : data.getSideHit();
            ICSideConfig config = ((IConfigurableSides)tile).sideConfig(side.getIndex());
            info.text(I18n.format(ICLib.DESC_INFO + "blockSide." + (flip ? "opposite" : "facing")) + ": " + I18n.format(ICLib.DESC_INFO + "blockSide.io." + (config.ordinal() - 1)));
        }
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
