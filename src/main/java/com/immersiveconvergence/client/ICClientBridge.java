package com.immersiveconvergence.client;

import com.immersiveconvergence.api.bridge.IICClientBridge;
import com.immersiveconvergence.common.client.IEClientBridge;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public final class ICClientBridge implements IICClientBridge {
    @Override @Nullable public FontRenderer itemFont() { return IEClientBridge.itemFont(); }

    @Override public void registerOBJLoader() { IEClientBridge.registerOBJLoader(); }

    @Override public void addOBJDomain(@Nonnull String modid) { IEClientBridge.addOBJDomain(modid); }

    @Override public void registerConnectorForRender(@Nonnull String key, @Nonnull ResourceLocation baseModel) { IEClientBridge.registerConnectorForRender(key, baseModel); }

    @Override public void clearConveyorModelCaches() throws ReflectiveOperationException { IEClientBridge.clearConveyorModelCaches(); }

    @Override public void addFluidTooltip(@Nonnull FluidStack fluid, @Nonnull List<String> tooltip, int tankCapacity) { IEClientBridge.addFluidTooltip(fluid, tooltip, tankCapacity); }

    @Override public boolean unformedMultiblockPart(@Nonnull TileEntity tile) { return IEClientBridge.unformedMultiblockPart(tile); }

    @Override @Nullable public BlockPos multiblockPartOffset(@Nonnull TileEntity tile) { return IEClientBridge.multiblockPartOffset(tile); }
}
