package com.immersiveconvergence.api.bridge;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface IICClientBridge {
    @Nullable FontRenderer itemFont();

    void registerOBJLoader();

    void addOBJDomain(@Nonnull String modid);

    void registerConnectorForRender(@Nonnull String key, @Nonnull ResourceLocation baseModel);

    void clearConveyorModelCaches() throws ReflectiveOperationException;

    void addFluidTooltip(@Nonnull FluidStack fluid, @Nonnull List<String> tooltip, int tankCapacity);

    boolean unformedMultiblockPart(@Nonnull TileEntity tile);

    @Nullable BlockPos multiblockPartOffset(@Nonnull TileEntity tile);
}
