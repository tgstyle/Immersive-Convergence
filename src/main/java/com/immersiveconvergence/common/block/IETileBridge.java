package com.immersiveconvergence.common.block;

import com.immersiveconvergence.api.energy.ICTileEntityConnectable;
import com.immersiveconvergence.api.client.IICOBJModelCallback;
import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.api.energy.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler;
import blusunrize.immersiveengineering.api.energy.wires.TileEntityImmersiveConnectable;
import blusunrize.immersiveengineering.api.shader.CapabilityShader;
import blusunrize.immersiveengineering.client.models.IOBJModelCallback;
import blusunrize.immersiveengineering.common.Config;
import blusunrize.immersiveengineering.common.blocks.TileEntityIEBase;
import blusunrize.immersiveengineering.common.util.inventory.IEInventoryHandler;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.Set;

public final class IETileBridge {
    private IETileBridge() {}

    public static void removeStateFromSmartModelCache(IExtendedBlockState state) { ImmersiveEngineering.proxy.removeStateFromSmartModelCache(state); }

    public static double renderDistanceMultiplier() { return Config.IEConfig.increasedTileRenderdistance; }

    @Nullable public static NonNullList<ItemStack> droppedItems(TileEntity tile) { return tile instanceof IIEInventory ? ((IIEInventory)tile).getDroppedItems() : null; }

    public static boolean isInventoryHandler(IItemHandler handler) { return handler instanceof IEInventoryHandler; }

    public static void clearConnections(TileEntity tile, World world, boolean dropWires) {
        if (tile instanceof IImmersiveConnectable) { ImmersiveNetHandler.INSTANCE.clearAllConnectionsFor(tile.getPos(), world, dropWires); }
    }

    public static void onEntityCollision(TileEntity tile, World world, Entity entity) {
        if (tile instanceof TileEntityIEBase) { ((TileEntityIEBase)tile).onEntityCollision(world, entity); }
    }

    private static <T> IEOBJCallbackAdapter<T> adapt(IICOBJModelCallback<T> callback) { return new IEOBJCallbackAdapter<>(callback); }

    public static net.minecraftforge.common.property.IUnlistedProperty<?> objCallbackProperty() { return IOBJModelCallback.PROPERTY; }

    public static net.minecraftforge.common.property.IUnlistedProperty<?> connectionsProperty() { return blusunrize.immersiveengineering.api.IEProperties.CONNECTIONS; }

    public static IExtendedBlockState extendState(IExtendedBlockState extended, TileEntity tile) {
        if (extended.getUnlistedNames().contains(IOBJModelCallback.PROPERTY)) {
            if (tile instanceof IOBJModelCallback) { extended = extended.withProperty(IOBJModelCallback.PROPERTY, (IOBJModelCallback<?>)tile); }
            else if (tile instanceof IICOBJModelCallback) { extended = extended.withProperty(IOBJModelCallback.PROPERTY, adapt((IICOBJModelCallback<?>)tile)); }
        }
        if (extended.getUnlistedNames().contains(blusunrize.immersiveengineering.api.IEProperties.CONNECTIONS)) {
            Set<?> conns = wireConnections(tile);
            if (conns != null) { extended = extended.withProperty(blusunrize.immersiveengineering.api.IEProperties.CONNECTIONS, conns); }
        }
        if (extended.getUnlistedNames().contains(CapabilityShader.BLOCKSTATE_PROPERTY) && tile.hasCapability(CapabilityShader.SHADER_CAPABILITY, null)) {
            extended = extended.withProperty(CapabilityShader.BLOCKSTATE_PROPERTY, tile.getCapability(CapabilityShader.SHADER_CAPABILITY, null));
        }
        return extended;
    }

    @Nullable public static Set<?> wireConnections(TileEntity tile) {
        if (tile instanceof TileEntityImmersiveConnectable) { return ((TileEntityImmersiveConnectable)tile).genConnBlockstate(); }
        if (tile instanceof ICTileEntityConnectable) { return ((ICTileEntityConnectable)tile).wireConnections(); }
        return null;
    }
}
