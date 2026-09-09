package com.immersiveconvergence.api.util;

import com.google.common.util.concurrent.ListenableFutureTask;

import com.immersiveconvergence.api.ICLib;
import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.block.ICBlockBase;
import com.immersiveconvergence.api.crafting.ICIngredient;
import com.immersiveconvergence.api.energy.IICFluxAcceptor;
import com.immersiveconvergence.common.util.IEChatBridge;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.init.Blocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.EnumSet;

@SuppressWarnings("unused")
public class ICUtils {
    public static void addFutureServerTask(World world, Runnable task) {
        MinecraftServer server = world.getMinecraftServer();
        if (server == null) { return; }
        synchronized (server.futureTaskQueue) { server.futureTaskQueue.add(ListenableFutureTask.create(task, null)); }
    }

    public static TileEntity getExistingTileEntity(World world, BlockPos pos) { return world != null && world.isBlockLoaded(pos) ? world.getTileEntity(pos) : null; }

    public static FluidStack copyFluidStackWithAmount(FluidStack stack, int amount, boolean stripPressure) {
        if (stack == null) { return null; }
        FluidStack copy = new FluidStack(stack, amount);
        if (stripPressure && copy.tag != null && copy.tag.hasKey("pressurized")) {
            copy.tag.removeTag("pressurized");
            if (copy.tag.isEmpty()) { copy.tag = null; }
        }
        return copy;
    }

    public static NonNullList<ItemStack> readInventory(NBTTagList nbt, int size) {
        NonNullList<ItemStack> inv = NonNullList.withSize(size, ItemStack.EMPTY);
        int max = nbt.tagCount();
        for (int i = 0; i < max; i++) {
            NBTTagCompound itemTag = nbt.getCompoundTagAt(i);
            int slot = itemTag.getByte("Slot") & 255;
            if (slot < size) { inv.set(slot, new ItemStack(itemTag)); }
        }
        return inv;
    }

    public static NonNullList<ItemStack> loadItemStacksFromNBT(NBTBase nbt) {
        NonNullList<ItemStack> itemStacks = NonNullList.create();
        if (nbt instanceof NBTTagCompound) {
            itemStacks.add(new ItemStack((NBTTagCompound)nbt));
            return itemStacks;
        }
        if (nbt instanceof NBTTagList) {
            NBTTagList list = (NBTTagList)nbt;
            return readInventory(list, list.tagCount());
        }
        return itemStacks;
    }

    public static NonNullList<ItemStack> createNonNullItemStackListFromItemStack(ItemStack stack) {
        NonNullList<ItemStack> list = NonNullList.withSize(1, ItemStack.EMPTY);
        list.set(0, stack);
        return list;
    }

    public static boolean compareItemNBT(ItemStack stack1, ItemStack stack2) {
        if (stack1.isEmpty() != stack2.isEmpty()) { return false; }
        boolean empty1 = stack1.getTagCompound() == null || stack1.getTagCompound().isEmpty();
        boolean empty2 = stack2.getTagCompound() == null || stack2.getTagCompound().isEmpty();
        if (empty1 != empty2) { return false; }
        if (!empty1 && !stack1.getTagCompound().equals(stack2.getTagCompound())) { return false; }
        return stack1.areCapsCompatible(stack2);
    }

    public static int calcRedstoneFromInventory(IICInventory inv) {
        if (inv == null) { return 0; }
        int max = inv.getComparatedSize();
        int filled = 0;
        float fraction = 0.0F;
        for (int j = 0; j < max; j++) {
            ItemStack itemstack = inv.getInventory().get(j);
            if (itemstack.isEmpty()) { continue; }
            fraction += (float)itemstack.getCount() / (float)Math.min(inv.getSlotLimit(j), itemstack.getMaxStackSize());
            filled++;
        }
        fraction = fraction / (float)max;
        return MathHelper.floor(fraction * 14.0F) + (filled > 0 ? 1 : 0);
    }

    public static boolean stacksMatchIngredientList(List<ICIngredient> list, NonNullList<ItemStack> stacks) {
        ArrayList<ItemStack> queryList = new ArrayList<>(stacks.size());
        for (ItemStack s : stacks) {
            if (!s.isEmpty()) { queryList.add(s.copy()); }
        }
        for (ICIngredient ingredient : list) {
            if (ingredient == null) { continue; }
            int amount = ingredient.inputSize;
            Iterator<ItemStack> it = queryList.iterator();
            while (it.hasNext()) {
                ItemStack query = it.next();
                if (query.isEmpty()) { continue; }
                if (ingredient.matchesItemStackIgnoringSize(query)) {
                    if (query.getCount() > amount) {
                        query.shrink(amount);
                        amount = 0;
                    }
                    else {
                        amount -= query.getCount();
                        query.setCount(0);
                    }
                }
                if (query.getCount() <= 0) { it.remove(); }
                if (amount <= 0) { break; }
            }
            if (amount > 0) { return false; }
        }
        return true;
    }

    public static NBTTagList writeInventory(Collection<ItemStack> inv) {
        NBTTagList invList = new NBTTagList();
        byte slot = 0;
        for (ItemStack s : inv) {
            if (!s.isEmpty()) {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setByte("Slot", slot);
                s.writeToNBT(itemTag);
                invList.appendTag(itemTag);
            }
            slot++;
        }
        return invList;
    }

    public static void dropStackAtPos(World world, BlockPos pos, ItemStack stack) { dropStackAtPos(world, pos, stack, null); }

    public static void dropStackAtPos(World world, BlockPos pos, ItemStack stack, EnumFacing facing) {
        if (stack.isEmpty()) { return; }
        EntityItem item = new EntityItem(world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, stack.copy());
        item.motionY = 0.025000000372529D;
        if (facing != null) {
            item.motionX = (0.075F * facing.getXOffset());
            item.motionZ = (0.075F * facing.getZOffset());
        }
        world.spawnEntity(item);
    }

    private static void stripEmptyTag(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && tag.isEmpty()) { stack.setTagCompound(null); }
    }

    public static ItemStack fillFluidContainer(IFluidHandler handler, ItemStack containerIn, ItemStack containerOut, @Nullable EntityPlayer player) {
        if (containerIn == null || containerIn.isEmpty()) { return ItemStack.EMPTY; }
        stripEmptyTag(containerIn);
        FluidActionResult result = FluidUtil.tryFillContainer(containerIn, handler, Integer.MAX_VALUE, player, false);
        if (result.isSuccess()) {
            final ItemStack full = result.getResult();
            if (containerOut.isEmpty() || OreDictionary.itemMatches(containerOut, full, true)) {
                if (!containerOut.isEmpty() && containerOut.getCount() + full.getCount() > containerOut.getMaxStackSize()) { return ItemStack.EMPTY; }
                result = FluidUtil.tryFillContainer(containerIn, handler, Integer.MAX_VALUE, player, true);
                if (result.isSuccess()) { return result.getResult(); }
            }
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack drainFluidContainer(IFluidHandler handler, ItemStack containerIn, ItemStack containerOut, @Nullable EntityPlayer player) {
        if (containerIn == null || containerIn.isEmpty()) { return ItemStack.EMPTY; }
        stripEmptyTag(containerIn);
        FluidActionResult result = FluidUtil.tryEmptyContainer(containerIn, handler, Integer.MAX_VALUE, player, false);
        if (result.isSuccess()) {
            ItemStack empty = result.getResult();
            if (containerOut.isEmpty() || OreDictionary.itemMatches(containerOut, empty, true)) {
                if (!containerOut.isEmpty() && containerOut.getCount() + empty.getCount() > containerOut.getMaxStackSize()) { return ItemStack.EMPTY; }
                result = FluidUtil.tryEmptyContainer(containerIn, handler, Integer.MAX_VALUE, player, true);
                if (result.isSuccess()) { return result.getResult(); }
            }
        }
        return ItemStack.EMPTY;
    }

    public static boolean isHammer(ItemStack stack) { return !stack.isEmpty() && stack.getItem().getToolClasses(stack).contains(ICLib.TOOL_HAMMER); }

    public static void modifyInvStackSize(NonNullList<ItemStack> inv, int slot, int amount) {
        if (slot >= 0 && slot < inv.size() && !inv.get(slot).isEmpty()) {
            inv.get(slot).grow(amount);
            if (inv.get(slot).getCount() <= 0) { inv.set(slot, ItemStack.EMPTY); }
        }
    }

    public static ItemStack insertStackIntoInventory(TileEntity inventory, ItemStack stack, EnumFacing side) {
        if (!stack.isEmpty() && inventory != null && inventory.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
            IItemHandler handler = inventory.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
            ItemStack temp = ItemHandlerHelper.insertItem(handler, stack.copy(), true);
            if (temp.isEmpty() || temp.getCount() < stack.getCount()) { return ItemHandlerHelper.insertItem(handler, stack, false); }
        }
        return stack;
    }

    public static boolean isFluidRelatedItemStack(ItemStack stack) { return !stack.isEmpty() && stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null); }

    public static boolean isFluidContainerFull(ItemStack stack) {
        if (stack.isEmpty()) { return false; }
        IFluidHandlerItem handler = FluidUtil.getFluidHandler(stack);
        if (handler == null) { return false; }
        for (IFluidTankProperties prop : handler.getTankProperties()) {
            if (prop.getContents() == null || prop.getContents().amount < prop.getCapacity()) { return false; }
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    public static IBlockState getStateFromItemStack(ItemStack stack) {
        if (stack.isEmpty()) { return null; }
        Block block = Block.getBlockFromItem(stack.getItem());
        return block != Blocks.AIR ? block.getStateFromMeta(stack.getItemDamage()) : null;
    }

    public static int insertFlux(TileEntity tile, EnumFacing facing, int energy, boolean simulate) {
        if (tile == null) { return 0; }
        if (tile instanceof IICFluxAcceptor && ((IICFluxAcceptor)tile).canConnectEnergy(facing)) { return ((IICFluxAcceptor)tile).receiveEnergy(facing, energy, simulate); }
        IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, facing);
        return storage == null ? 0 : storage.receiveEnergy(energy, simulate);
    }

    public static NonNullList<ItemStack> fromItems(ItemStack... itemStack) {
        NonNullList<ItemStack> list = NonNullList.create();
        for (int i = 0; i < itemStack.length; i++) { list.add(i, itemStack[i] != null ? itemStack[i] : ItemStack.EMPTY); }
        return list;
    }

    public static boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if ((block == Blocks.PISTON || block == Blocks.STICKY_PISTON) && state.getValue(BlockPistonBase.EXTENDED)) { return false; }
        if (block == Blocks.BED || block == Blocks.END_PORTAL_FRAME || block == Blocks.SKULL) { return false; }
        if (block.hasTileEntity(state)) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileEntityChest) {
                TileEntityChest chest = (TileEntityChest)tile;
                if (chest.adjacentChestXNeg == null && chest.adjacentChestXPos == null && chest.adjacentChestZNeg == null && chest.adjacentChestZPos == null) { return false; }
            }
        }
        return block.rotateBlock(world, pos, axis);
    }

    public static void sendServerNoSpamMessages(EntityPlayer player, ITextComponent... messages) {
        if (messages.length < 1) { return; }
        if (ICMods.immersiveEngineering()) {
            IEChatBridge.sendServerNoSpamMessages(player, messages);
            return;
        }
        for (ITextComponent message : messages) { player.sendStatusMessage(message, true); }
    }

    @SuppressWarnings("deprecation") public static IBlockState stateOf(Block block, int meta) { return block.getStateFromMeta(meta); }

    public static IBlockState stateOf(ICBlockBase<?> block, ICBlockBase.IBlockEnum type) { return block.getStateFromMeta(type.getMeta()); }

    public static void improvedMarkBlockForUpdate(World world, BlockPos pos, @Nullable IBlockState newState, EnumSet<EnumFacing> directions) {
        IBlockState state = world.getBlockState(pos);
        if (newState == null) { newState = state; }
        world.notifyBlockUpdate(pos, state, newState, 3);
        if (!ForgeEventFactory.onNeighborNotify(world, pos, newState, EnumSet.allOf(EnumFacing.class), true).isCanceled()) {
            Block blockType = newState.getBlock();
            for (EnumFacing facing : directions) {
                BlockPos toNotify = pos.offset(facing);
                if (world.isBlockLoaded(toNotify)) { world.neighborChanged(toNotify, blockType, pos); }
            }
            world.updateObservingBlocksAt(pos, blockType);
        }
    }
}
