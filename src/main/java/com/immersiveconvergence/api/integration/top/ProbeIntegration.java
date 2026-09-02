package com.immersiveconvergence.api.integration.top;

import com.immersiveconvergence.api.integration.DisplayContexts;
import com.immersiveconvergence.api.integration.DisplayLines;
import com.immersiveconvergence.api.multiblock.IDisplayContext;
import com.immersiveconvergence.core.lib.ICLib;

import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.api.energy.AveragingEnergyStorage;
import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.IProgressStyle;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.NumberFormat;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;

import javax.annotation.Nullable;
import java.util.function.Function;

public class ProbeIntegration {
    public static void enqueueIMC() {
        if (!ModList.get().isLoaded("theoneprobe")) { return; }
        InterModComms.sendTo("theoneprobe", "getTheOneProbe", () -> (Function<ITheOneProbe, Void>) top -> {
            top.registerProvider(new Provider());
            return null;
        });
    }

    public static class Provider implements IProbeInfoProvider {
        @Override public ResourceLocation getID() { return ICLib.rl("display"); }

        @Override public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level level, BlockState blockState, IProbeHitData data) {
            IDisplayContext context = DisplayContexts.of(level.getBlockEntity(data.getPos()));
            if (context == null) { return; }
            for (IFluidTank tank : context.getInternalTanks()) { if (tank != null) { addFluidTank(probeInfo, tank); } }
            for (AveragingEnergyStorage energy : context.getEnergies()) { addEnergy(probeInfo, energy.getEnergyStored(), energy.getMaxEnergyStored()); }
            DisplayLines lines = new DisplayLines();
            context.addDisplayLines(level, lines);
            for (DisplayLines.Line line : lines.lines()) {
                if (line instanceof DisplayLines.Text(
                        net.minecraft.network.chat.Component text1
                )) { probeInfo.text(text1); }
                else if (line instanceof DisplayLines.Progress progress) { addProgress(probeInfo, progress); }
            }
        }
    }

    private static IProbeInfo row(IProbeInfo probeInfo) { return probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER).spacing(2)); }

    private static void addFluidTank(IProbeInfo probeInfo, IFluidTank tank) {
        FluidStack fluid = tank.getFluid();
        int amount = !fluid.isEmpty() ? fluid.getAmount() : 0;
        String fluidName = !fluid.isEmpty() ? fluid.getHoverName().getString() : "Empty";
        int color = getFluidColor(fluid);
        row(probeInfo).progress(amount, tank.getCapacity(), probeInfo.defaultProgressStyle().suffix(" mB").numberFormat(NumberFormat.COMPACT).filledColor(color).alternateFilledColor(color).backgroundColor(0xff000000).borderColor(0xffffffff)).text(fluidName);
    }

    private static void addEnergy(IProbeInfo probeInfo, int stored, int max) { row(probeInfo).progress(stored, max, probeInfo.defaultProgressStyle().suffix(" IF").filledColor(Lib.COLOUR_I_ImmersiveOrange).alternateFilledColor(0xff994f20).borderColor(Lib.COLOUR_I_ImmersiveOrangeShadow).numberFormat(NumberFormat.COMPACT)); }

    private static void addProgress(IProbeInfo probeInfo, DisplayLines.Progress progress) {
        IProgressStyle style = probeInfo.defaultProgressStyle().suffix(progress.suffix()).numberFormat(progress.compact() ? NumberFormat.COMPACT : NumberFormat.FULL);
        if (progress.fillColor() != DisplayLines.DEFAULT_COLOR) { style = style.filledColor(progress.fillColor()).alternateFilledColor(progress.fillColor()); }
        if (progress.borderColor() != DisplayLines.DEFAULT_COLOR) { style = style.borderColor(progress.borderColor()); }
        row(probeInfo).progress(progress.value(), progress.max(), style);
    }

    @SuppressWarnings("resource")
    private static int getFluidColor(@Nullable FluidStack fluid) {
        if (fluid == null || fluid.isEmpty()) { return 0xff555555; }
        IClientFluidTypeExtensions ext = IClientFluidTypeExtensions.of(fluid.getFluid());
        int tint = ext.getTintColor(fluid);
        if (!FMLEnvironment.dist.isClient()) { return 0xff000000 | (tint & 0x00ffffff); }
        ResourceLocation still = ext.getStillTexture(fluid);
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(still);
        if (sprite == null) { return 0xff000000 | (tint & 0x00ffffff); }
        int width = sprite.contents().width();
        int height = sprite.contents().height();
        int avgR = 0, avgG = 0, avgB = 0, count = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int p = sprite.getPixelRGBA(0, x, y);
                int alpha = (p >> 24) & 0xff;
                if (alpha > 0) {
                    avgR += ((p & 0xff) * alpha) / 255;
                    avgG += (((p >> 8) & 0xff) * alpha) / 255;
                    avgB += (((p >> 16) & 0xff) * alpha) / 255;
                    count++;
                }
            }
        }
        if (count == 0) { return 0xff000000 | (tint & 0x00ffffff); }
        avgR /= count;
        avgG /= count;
        avgB /= count;
        int r = (avgR * ((tint >> 16) & 0xff)) / 255;
        int g = (avgG * ((tint >> 8) & 0xff)) / 255;
        int b = (avgB * (tint & 0xff)) / 255;
        return 0xff000000 | r << 16 | g << 8 | b;
    }
}
