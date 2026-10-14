package com.immersiveconvergence.api.integration.top;

import com.immersiveconvergence.api.block.BlockInterfaces;
import com.immersiveconvergence.api.block.Enums;
import com.immersiveconvergence.api.integration.DisplayContexts;
import com.immersiveconvergence.api.integration.DisplayLines;
import com.immersiveconvergence.api.multiblock.IDisplayContext;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockBE;
import com.immersiveconvergence.core.lib.ICLib;

import mcjty.theoneprobe.Tools;
import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IBlockDisplayOverride;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.IProgressStyle;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.NumberFormat;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.api.TextStyleClass;
import mcjty.theoneprobe.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;

import javax.annotation.Nullable;
import java.util.function.Function;

public class ProbeIntegration {
    public static final String MODID = "theoneprobe";

    public static void enqueueIMC() {
        InterModComms.sendTo(MODID, "getTheOneProbe", () -> (Function<ITheOneProbe, Void>) top -> {
            top.registerProvider(new Provider());
            top.registerBlockDisplayOverride(new MultiblockDisplayOverride());
            return null;
        });
    }

    public static class Provider implements IProbeInfoProvider {
        @Override public ResourceLocation getID() { return ICLib.rl("display"); }

        @Override public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level level, BlockState blockState, IProbeHitData data) {
            BlockEntity blockEntity = level.getBlockEntity(data.getPos());
            IDisplayContext context = DisplayContexts.of(blockEntity);
            if (context != null) { addContext(probeInfo, level, context); }
            if (blockEntity instanceof BlockInterfaces.IConfigurableSides sides && data.getSideHit() != null) { addSideConfig(probeInfo, player, sides, data.getSideHit()); }
        }
    }

    public static class MultiblockDisplayOverride implements IBlockDisplayOverride {
        @Override public boolean overrideStandardInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level level, BlockState blockState, IProbeHitData data) {
            if (!(level.getBlockEntity(data.getPos()) instanceof IMultiblockBE<?>)) { return false; }
            ItemStack stack = new ItemStack(blockState.getBlock(), 1);
            if (Tools.show(mode, Config.getRealConfig().getShowModName())) { probeInfo.horizontal().item(stack).vertical().itemLabel(stack).text(Component.literal(TextStyleClass.MODNAME + Tools.getModName(blockState.getBlock()))); }
            else { probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER)).item(stack).itemLabel(stack); }
            return true;
        }
    }

    private static void addContext(IProbeInfo probeInfo, Level level, IDisplayContext context) {
        for (IFluidTank tank : context.getInternalTanks()) { if (tank != null) { addFluidTank(probeInfo, tank); } }
        for (IEnergyStorage energy : context.getEnergies()) { addEnergy(probeInfo, energy.getEnergyStored(), energy.getMaxEnergyStored()); }
        DisplayLines lines = new DisplayLines();
        context.addDisplayLines(level, lines);
        for (DisplayLines.Line line : lines.lines()) {
            if (line instanceof DisplayLines.Text(Component text)) { probeInfo.text(text); }
            else if (line instanceof DisplayLines.Progress progress) { addProgress(probeInfo, progress); }
        }
    }

    private static void addSideConfig(IProbeInfo probeInfo, Player player, BlockInterfaces.IConfigurableSides sides, Direction sideHit) {
        boolean flip = player.isShiftKeyDown();
        Enums.IOSideConfig config = sides.getSideConfig(flip ? sideHit.getOpposite() : sideHit);
        probeInfo.text(Component.empty().append(Component.translatable(ICLib.DESC_INFO + "blockSide." + (flip ? "opposite" : "facing"))).append(": ").append(config.getTextComponent()));
    }

    private static IProbeInfo row(IProbeInfo probeInfo) { return probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER).spacing(2)); }

    private static void addFluidTank(IProbeInfo probeInfo, IFluidTank tank) {
        FluidStack fluid = tank.getFluid();
        int amount = !fluid.isEmpty() ? fluid.getAmount() : 0;
        String fluidName = !fluid.isEmpty() ? fluid.getHoverName().getString() : "Empty";
        int color = getFluidColor(fluid);
        row(probeInfo).progress(amount, tank.getCapacity(), probeInfo.defaultProgressStyle().suffix(" mB").numberFormat(NumberFormat.COMPACT).filledColor(color).alternateFilledColor(color).backgroundColor(0xff000000).borderColor(0xffffffff)).text(fluidName);
    }

    private static void addEnergy(IProbeInfo probeInfo, int stored, int max) { row(probeInfo).progress(stored, max, probeInfo.defaultProgressStyle().suffix(" IF").filledColor(ICLib.COLOR_ORANGE).alternateFilledColor(0xff994f20).borderColor(ICLib.COLOR_ORANGE_SHADOW).numberFormat(NumberFormat.COMPACT)); }

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
