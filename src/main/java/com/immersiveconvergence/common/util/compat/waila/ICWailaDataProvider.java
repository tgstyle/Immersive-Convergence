package com.immersiveconvergence.common.util.compat.waila;

import com.immersiveconvergence.api.energy.IICFluxAcceptor;
import com.immersiveconvergence.api.energy.IICFluxProvider;
import com.immersiveconvergence.api.multiblock.ICTileEntityMultiblockPart;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.ITaggedList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

@SuppressWarnings("unused")
public class ICWailaDataProvider implements IWailaDataProvider {

    private static final String TAG_ENERGY = "IFEnergyStorage";

    public static void callbackRegister(IWailaRegistrar registrar) {
        ICWailaDataProvider provider = new ICWailaDataProvider();
        registrar.registerStackProvider(provider, ICTileEntityMultiblockPart.class);
        registrar.registerBodyProvider(provider, IICFluxAcceptor.class);
        registrar.registerNBTProvider(provider, IICFluxAcceptor.class);
        registrar.registerBodyProvider(provider, IICFluxProvider.class);
        registrar.registerNBTProvider(provider, IICFluxProvider.class);
    }

    @Override @Nonnull public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getTileEntity() instanceof ICTileEntityMultiblockPart) { return new ItemStack(accessor.getBlock(), 1, accessor.getMetadata()); }
        return ItemStack.EMPTY;
    }

    @Override @Nonnull public List<String> getWailaHead(ItemStack stack, List<String> tip, IWailaDataAccessor accessor, IWailaConfigHandler config) { return tip; }

    @Override @Nonnull public List<String> getWailaBody(ItemStack stack, List<String> tip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        int stored = accessor.getNBTInteger(accessor.getNBTData(), "Energy");
        int max = accessor.getNBTInteger(accessor.getNBTData(), "MaxStorage");
        if (max > 0 && tip instanceof ITaggedList) {
            ITaggedList<String, String> tagged = (ITaggedList<String, String>)tip;
            if (tagged.getEntries(TAG_ENERGY).isEmpty()) { tagged.add(String.format("%d / %d IF", stored, max), TAG_ENERGY); }
        }
        return tip;
    }

    @Override @Nonnull public List<String> getWailaTail(ItemStack stack, List<String> tip, IWailaDataAccessor accessor, IWailaConfigHandler config) { return tip; }

    @Override @Nonnull public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, BlockPos pos) {
        int stored = -1;
        int max = -1;
        if (tile instanceof IICFluxAcceptor) {
            stored = ((IICFluxAcceptor)tile).getEnergyStored(null);
            max = ((IICFluxAcceptor)tile).getMaxEnergyStored(null);
        }
        else if (tile instanceof IICFluxProvider) {
            stored = ((IICFluxProvider)tile).getEnergyStored(null);
            max = ((IICFluxProvider)tile).getMaxEnergyStored(null);
        }
        if (stored != -1) {
            tag.setInteger("Energy", stored);
            tag.setInteger("MaxStorage", max);
        }
        return tag;
    }
}
