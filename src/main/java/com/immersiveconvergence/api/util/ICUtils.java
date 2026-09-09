package com.immersiveconvergence.api.util;

import blusunrize.immersiveengineering.common.util.ChatUtils;
import blusunrize.immersiveengineering.common.util.EnergyHelper;
import blusunrize.immersiveengineering.common.util.ListUtils;
import blusunrize.immersiveengineering.common.util.RotationUtil;
import blusunrize.immersiveengineering.common.util.Utils;
import com.immersiveconvergence.api.block.ICBlockBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.EnumSet;

@SuppressWarnings("unused")
public class ICUtils {
    public static TileEntity getExistingTileEntity(World world, BlockPos pos) { return Utils.getExistingTileEntity(world, pos); }

    public static FluidStack copyFluidStackWithAmount(FluidStack stack, int amount, boolean stripPressure) { return Utils.copyFluidStackWithAmount(stack, amount, stripPressure); }

    public static NonNullList<ItemStack> readInventory(NBTTagList nbt, int size) { return Utils.readInventory(nbt, size); }

    public static NBTTagList writeInventory(Collection<ItemStack> inv) { return Utils.writeInventory(inv); }

    public static void dropStackAtPos(World world, BlockPos pos, ItemStack stack) { Utils.dropStackAtPos(world, pos, stack); }

    public static void dropStackAtPos(World world, BlockPos pos, ItemStack stack, EnumFacing facing) { Utils.dropStackAtPos(world, pos, stack, facing); }

    public static ItemStack fillFluidContainer(IFluidHandler handler, ItemStack containerIn, ItemStack containerOut, @Nullable EntityPlayer player) { return Utils.fillFluidContainer(handler, containerIn, containerOut, player); }

    public static ItemStack drainFluidContainer(IFluidHandler handler, ItemStack containerIn, ItemStack containerOut, @Nullable EntityPlayer player) { return Utils.drainFluidContainer(handler, containerIn, containerOut, player); }

    public static boolean isHammer(ItemStack stack) { return Utils.isHammer(stack); }

    public static void modifyInvStackSize(NonNullList<ItemStack> inv, int slot, int amount) { Utils.modifyInvStackSize(inv, slot, amount); }

    public static ItemStack insertStackIntoInventory(TileEntity inventory, ItemStack stack, EnumFacing side) { return Utils.insertStackIntoInventory(inventory, stack, side); }

    public static boolean isFluidRelatedItemStack(ItemStack stack) { return Utils.isFluidRelatedItemStack(stack); }

    public static boolean isFluidContainerFull(ItemStack stack) { return Utils.isFluidContainerFull(stack); }

    public static IBlockState getStateFromItemStack(ItemStack stack) { return Utils.getStateFromItemStack(stack); }

    public static int insertFlux(TileEntity tile, EnumFacing facing, int energy, boolean simulate) { return EnergyHelper.insertFlux(tile, facing, energy, simulate); }

    public static NonNullList<ItemStack> fromItems(ItemStack... itemStack) { return ListUtils.fromItems(itemStack); }

    public static boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) { return RotationUtil.rotateBlock(world, pos, axis); }

    public static void sendServerNoSpamMessages(EntityPlayer player, ITextComponent... messages) { ChatUtils.sendServerNoSpamMessages(player, messages); }

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
