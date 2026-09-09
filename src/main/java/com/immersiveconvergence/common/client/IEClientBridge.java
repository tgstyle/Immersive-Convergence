package com.immersiveconvergence.common.client;

import blusunrize.immersiveengineering.api.energy.wires.WireApi;
import blusunrize.immersiveengineering.client.ClientProxy;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.models.ModelConveyor;
import blusunrize.immersiveengineering.client.models.obj.IEOBJLoader;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IAdvancedSelectionBounds;
import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;

import net.minecraftforge.client.model.ModelLoaderRegistry;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public final class IEClientBridge {
    private IEClientBridge() {}

    public static FontRenderer itemFont() { return ClientProxy.itemFont; }

    public static void registerOBJLoader() { ModelLoaderRegistry.registerLoader(IEOBJLoader.instance); }

    public static void addOBJDomain(String modid) { IEOBJLoader.instance.addDomain(modid); }

    @SuppressWarnings("deprecation")
    public static void registerConnectorForRender(String key, ResourceLocation baseModel) { WireApi.registerConnectorForRender(key, baseModel, null); }

    public static void clearConveyorModelCaches() throws ReflectiveOperationException {
        ModelConveyor.modelCache.clear();
        Field itemCacheField = ModelConveyor.class.getDeclaredField("itemModelCache");
        itemCacheField.setAccessible(true);
        ((Map<?, ?>)itemCacheField.get(null)).clear();
    }

    public static void addFluidTooltip(FluidStack fluid, List<String> tooltip, int tankCapacity) { ClientUtils.addFluidTooltip(fluid, tooltip, tankCapacity); }

    @Nullable public static List<AxisAlignedBB> advancedSelectionBounds(TileEntity te) {
        return te instanceof IAdvancedSelectionBounds ? ((IAdvancedSelectionBounds)te).getAdvancedSelectionBounds() : null;
    }

    @Nullable public static BlockPos multiblockPartOffset(TileEntity te) {
        if (!(te instanceof TileEntityMultiblockPart) || !((TileEntityMultiblockPart<?>)te).formed) { return null; }
        int[] offset = ((TileEntityMultiblockPart<?>)te).offset;
        return new BlockPos(offset[0], offset[1], offset[2]);
    }
}
