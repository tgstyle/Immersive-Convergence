package com.immersiveconvergence.api.block;

import com.immersiveconvergence.ImmersiveConvergence;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces;
import com.immersiveconvergence.api.multiblock.ICTileEntityMultiblockMetal;
import com.immersiveconvergence.api.multiblock.ICTileEntityMultiblockPart;
import com.immersiveconvergence.api.util.ICInventoryHandler;
import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.util.ICDimensionBlockPos;
import com.immersiveconvergence.api.util.ICUtils;
import com.immersiveconvergence.common.block.IETileBridge;
import com.immersiveconvergence.api.util.IICInventory;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.*;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"deprecation", "unused"})
@Mod.EventBusSubscriber(modid = ImmersiveConvergence.MODID)
public abstract class ICBlockTileProvider<E extends Enum<E> & ICBlockBase.IBlockEnum> extends ICBlockBase<E> {
    private static final Map<ICDimensionBlockPos, TileEntity> tempTile = new HashMap<>();

    public ICBlockTileProvider(BlockContext context, String name, Material material, PropertyEnum<E> mainProperty, Class<? extends ItemBlock> itemBlock, Object... additionalProperties) {
        super(context, name, material, mainProperty, itemBlock, additionalProperties);
    }

    @SubscribeEvent public static void onTick(TickEvent.ServerTickEvent ev) {
        if (ev.phase == TickEvent.Phase.END) { tempTile.clear(); }
    }

    @Override public boolean hasTileEntity(@Nonnull IBlockState state) { return true; }

    @Override @Nullable public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        TileEntity basic = createBasicTE(world, state.getValue(property));
        Collection<IProperty<?>> keys = state.getPropertyKeys();
        if (basic instanceof IDirectionalTile) {
            EnumFacing newFacing = null;
            if (keys.contains(ICProperties.FACING_HORIZONTAL)) { newFacing = state.getValue(ICProperties.FACING_HORIZONTAL); }
            else if (keys.contains(ICProperties.FACING_ALL)) { newFacing = state.getValue(ICProperties.FACING_ALL); }
            int type = ((IDirectionalTile)basic).getFacingLimitation();
            if (newFacing != null) {
                switch (type) {
                    case 2:
                    case 4:
                    case 5:
                    case 6:
                        if (newFacing.getAxis() == Axis.Y) { newFacing = null; }
                        break;
                    case 3:
                        if (newFacing.getAxis() != Axis.Y) { newFacing = null; }
                        break;
                }
                if (newFacing != null) { ((IDirectionalTile)basic).setFacing(newFacing); }
            }
        }
        if (basic instanceof IAttachedIntegerProperties) {
            IAttachedIntegerProperties tileIntProps = (IAttachedIntegerProperties)basic;
            String[] names = tileIntProps.getIntPropertyNames();
            for (String propertyName : names) {
                PropertyInteger property = tileIntProps.getIntProperty(propertyName);
                if (keys.contains(property)) { tileIntProps.setValue(propertyName, state.getValue(property)); }
            }
        }
        return basic;
    }

    @Override protected IBlockState getInitDefaultState() {
        IBlockState ret = super.getInitDefaultState();
        if (ret.getPropertyKeys().contains(ICProperties.FACING_ALL)) { ret = ret.withProperty(ICProperties.FACING_ALL, getDefaultFacing()); }
        else if (ret.getPropertyKeys().contains(ICProperties.FACING_HORIZONTAL)) { ret = ret.withProperty(ICProperties.FACING_HORIZONTAL, getDefaultFacing()); }
        return ret;
    }

    @Nullable public abstract TileEntity createBasicTE(World worldIn, E type);

    @Override public void getDrops(@Nonnull NonNullList<ItemStack> drops, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int fortune) {
        TileEntity tile = world.getTileEntity(pos);
        ICDimensionBlockPos dpos = new ICDimensionBlockPos(pos, world instanceof World ? ((World)world).provider.getDimension() : 0);
        if (tile == null) { tile = tempTile.get(dpos); }
        if (tile != null && (!(tile instanceof ITileDrop) || !((ITileDrop)tile).preventInventoryDrop())) {
            NonNullList<ItemStack> dropped = tile instanceof IICInventory ? ((IICInventory)tile).getDroppedItems()
                    : ICMods.immersiveEngineering() ? IETileBridge.droppedItems(tile) : null;
            if (dropped != null) {
                for (ItemStack s : dropped) { if (!s.isEmpty()) { drops.add(s); } }
            } else if (tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
                IItemHandler h = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                if (h instanceof ICInventoryHandler || (ICMods.immersiveEngineering() && IETileBridge.isInventoryHandler(h))) {
                    for (int i = 0; i < h.getSlots(); i++) {
                        if (!h.getStackInSlot(i).isEmpty()) {
                            drops.add(h.getStackInSlot(i));
                            ((IItemHandlerModifiable)h).setStackInSlot(i, ItemStack.EMPTY);
                        }
                    }
                }
            }
        }
        if (tile instanceof ITileDrop) {
            NonNullList<ItemStack> s = ((ITileDrop)tile).getTileDrops(harvesters.get(), state);
            drops.addAll(s);
        } else { super.getDrops(drops, world, pos, state, fortune); }
        tempTile.remove(dpos);
    }

    @Override public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof IHasDummyBlocks) { ((IHasDummyBlocks)tile).breakDummies(pos, state); }
        if (tile != null && !world.isRemote && ICMods.immersiveEngineering()) { IETileBridge.clearConnections(tile, world, world.getGameRules().getBoolean("doTileDrops")); }
        tempTile.put(new ICDimensionBlockPos(pos, world.provider.getDimension()), tile);
        super.breakBlock(world, pos, state);
        world.removeTileEntity(pos);
    }

    @Override public void harvestBlock(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nullable TileEntity tile, @Nonnull ItemStack stack) {
        if (tile instanceof ITileDrop) {
            ItemStack s = ((ITileDrop)tile).getTileDrop(player, state);
            if (!s.isEmpty()) { spawnAsEntity(world, pos, s); return; }
        }
        if (tile instanceof IAdditionalDrops) {
            Collection<ItemStack> stacks = ((IAdditionalDrops)tile).getExtraDrops(player, state);
            if (!stacks.isEmpty()) {
                for (ItemStack s : stacks) { if (!s.isEmpty()) { spawnAsEntity(world, pos, s); } }
            }
        }
        super.harvestBlock(world, player, pos, state, tile, stack);
    }

    @Override public boolean canEntityDestroy(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull Entity entity) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof IEntityProof) { return ((IEntityProof)tile).canEntityDestroy(entity); }
        return super.canEntityDestroy(state, world, pos, entity);
    }

    @Override @Nonnull public ItemStack getPickBlock(@Nonnull IBlockState state, @Nonnull RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof ITileDrop) {
            ItemStack s = ((ITileDrop)tile).getTileDrop(player, world.getBlockState(pos));
            if (!s.isEmpty()) { return s; }
        }
        Item item = Item.getItemFromBlock(this);
        return item == Items.AIR ? ItemStack.EMPTY : new ItemStack(item, 1, this.damageDropped(world.getBlockState(pos)));
    }

    @Override public boolean eventReceived(@Nonnull IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, int eventID, int eventParam) {
        super.eventReceived(state, worldIn, pos, eventID, eventParam);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(eventID, eventParam);
    }

    protected EnumFacing getDefaultFacing() { return EnumFacing.NORTH; }

    @Override @Nonnull public IBlockState getActualState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        state = super.getActualState(state, world, pos);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof IAttachedIntegerProperties) {
            for (String s : ((IAttachedIntegerProperties)tile).getIntPropertyNames()) {
                state = applyProperty(state, ((IAttachedIntegerProperties)tile).getIntProperty(s), ((IAttachedIntegerProperties)tile).getIntPropertyValue(s));
            }
        }
        if (tile instanceof IDirectionalTile && (state.getPropertyKeys().contains(ICProperties.FACING_ALL) || state.getPropertyKeys().contains(ICProperties.FACING_HORIZONTAL))) {
            PropertyDirection prop = state.getPropertyKeys().contains(ICProperties.FACING_HORIZONTAL) ? ICProperties.FACING_HORIZONTAL : ICProperties.FACING_ALL;
            state = applyProperty(state, prop, ((IDirectionalTile)tile).getFacing());
        } else if (state.getPropertyKeys().contains(ICProperties.FACING_HORIZONTAL)) { state = state.withProperty(ICProperties.FACING_HORIZONTAL, getDefaultFacing()); }
        else if (state.getPropertyKeys().contains(ICProperties.FACING_ALL)) { state = state.withProperty(ICProperties.FACING_ALL, getDefaultFacing()); }
        if (tile instanceof IActiveState) {
            IProperty<?> boolProp = ((IActiveState)tile).getBoolProperty(IActiveState.class);
            if (state.getPropertyKeys().contains(boolProp)) { state = applyProperty(state, boolProp, ((IActiveState)tile).getIsActive()); }
        }
        if (tile instanceof IDualState) {
            IProperty<?> boolProp = ((IDualState)tile).getBoolProperty(IDualState.class);
            if (state.getPropertyKeys().contains(boolProp)) { state = applyProperty(state, boolProp, ((IDualState)tile).getIsSecondState()); }
        }
        if (tile instanceof ICTileEntityMultiblockPart) { state = applyProperty(state, ICProperties.MULTIBLOCKSLAVE, ((ICTileEntityMultiblockPart<?>)tile).isDummy()); }
        else if (tile instanceof IHasDummyBlocks) { state = applyProperty(state, ICProperties.MULTIBLOCKSLAVE, ((IHasDummyBlocks)tile).isDummy()); }
        if (tile instanceof IMirrorAble) { state = applyProperty(state, ((IMirrorAble)tile).getBoolProperty(IMirrorAble.class), ((IMirrorAble)tile).getIsMirrored()); }
        return state;
    }

    @Override public boolean rotateBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing axis) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof IDirectionalTile) {
            if (((IDirectionalTile)tile).cannotRotate(axis)) { return false; }
            IBlockState state = world.getBlockState(pos);
            if (state.getPropertyKeys().contains(ICProperties.FACING_ALL) || state.getPropertyKeys().contains(ICProperties.FACING_HORIZONTAL)) {
                PropertyDirection prop = state.getPropertyKeys().contains(ICProperties.FACING_HORIZONTAL) ? ICProperties.FACING_HORIZONTAL : ICProperties.FACING_ALL;
                EnumFacing f = ((IDirectionalTile)tile).getFacing();
                int limit = ((IDirectionalTile)tile).getFacingLimitation();
                if (limit == 0) { f = EnumFacing.VALUES[(f.ordinal() + 1) % EnumFacing.VALUES.length]; }
                else if (limit == 1) { f = axis.getAxisDirection() == AxisDirection.POSITIVE ? f.rotateAround(axis.getAxis()).getOpposite() : f.rotateAround(axis.getAxis()); }
                else if (limit == 2 || limit == 5) { f = axis.getAxisDirection() == AxisDirection.POSITIVE ? f.rotateY() : f.rotateYCCW(); }
                if (f != ((IDirectionalTile)tile).getFacing()) {
                    EnumFacing old = ((IDirectionalTile)tile).getFacing();
                    ((IDirectionalTile)tile).setFacing(f);
                    ((IDirectionalTile)tile).afterRotation(old, f);
                    state = applyProperty(state, prop, ((IDirectionalTile)tile).getFacing());
                    world.setBlockState(pos, state.cycleProperty(prop));
                }
            }
        }
        return false;
    }

    @Override @Nonnull public IBlockState getExtendedState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        state = super.getExtendedState(state, world, pos);
        if (state instanceof IExtendedBlockState) {
            IExtendedBlockState extended = (IExtendedBlockState)state;
            TileEntity te = world.getTileEntity(pos);
            if (te != null) {
                if (te instanceof IConfigurableSides) {
                    for (int i = 0; i < 6; i++) {
                        if (extended.getUnlistedNames().contains(ICProperties.SIDECONFIG[i])) {
                            extended = extended.withProperty(ICProperties.SIDECONFIG[i], ((IConfigurableSides)te).sideConfig(i));
                        }
                    }
                }
                if (te instanceof IAdvancedHasObjProperty) { extended = extended.withProperty(Properties.AnimationProperty, ((IAdvancedHasObjProperty)te).getOBJState()); }
                else if (te instanceof IHasObjProperty) { extended = extended.withProperty(Properties.AnimationProperty, new OBJModel.OBJState(((IHasObjProperty)te).compileDisplayList(), true)); }
                if (te instanceof IDynamicTexture) { extended = extended.withProperty(ICProperties.OBJ_TEXTURE_REMAP, ((IDynamicTexture)te).getTextureReplacements()); }
                if (ICMods.immersiveEngineering()) { extended = IETileBridge.extendState(extended, te); }
                if (te instanceof IPropertyPassthrough && ((IExtendedBlockState)state).getUnlistedNames().contains(ICProperties.TILEENTITY_PASSTHROUGH)) { extended = extended.withProperty(ICProperties.TILEENTITY_PASSTHROUGH, te); }
                if (ICMods.immersiveEngineering() && ((IExtendedBlockState)state).getUnlistedNames().contains(ICProperties.CONNECTIONS)) {
                    Set<?> connections = IETileBridge.wireConnections(te);
                    if (connections != null) { extended = extended.withProperty(ICProperties.CONNECTIONS, connections); }
                }
            }
            state = extended;
        }
        return state;
    }

    @Override public void onITBlockPlacedBy(World world, BlockPos pos, IBlockState state, EnumFacing side, float hitX, float hitY, float hitZ, EntityLivingBase placer, ItemStack stack) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof IDirectionalTile) {
            EnumFacing f = ((IDirectionalTile)tile).getFacingForPlacement(placer, pos, side, hitX, hitY, hitZ);
            ((IDirectionalTile)tile).setFacing(f);
            if (tile instanceof IAdvancedDirectionalTile) { ((IAdvancedDirectionalTile)tile).onDirectionalPlacement(side, hitX, hitY, hitZ, placer); }
        }
        if (tile instanceof ITileDrop) { ((ITileDrop)tile).readOnPlacement(placer, stack); }
        if (tile instanceof IHasDummyBlocks) { ((IHasDummyBlocks)tile).placeDummies(pos, state, side, hitX, hitY, hitZ); }
        if (tile instanceof IPlacementInteraction) { ((IPlacementInteraction)tile).onTilePlaced(world, pos, state, side, hitX, hitY, hitZ, placer, stack); }
    }

    @Override public boolean onBlockActivated(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = player.getHeldItem(hand);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof IConfigurableSides && ICUtils.isHammer(heldItem) && !world.isRemote) {
            int iSide = player.isSneaking() ? side.getOpposite().ordinal() : side.ordinal();
            if (((IConfigurableSides)tile).toggleSide(iSide, player)) { return true; }
        }
        if (tile instanceof IDirectionalTile && ICUtils.isHammer(heldItem) && ((IDirectionalTile)tile).canHammerRotate(side, hitX, hitY, hitZ, player) && !world.isRemote) {
            EnumFacing f = ((IDirectionalTile)tile).getFacing();
            EnumFacing oldF = f;
            int limit = ((IDirectionalTile)tile).getFacingLimitation();
            if (limit == 0) { f = EnumFacing.VALUES[(f.ordinal() + 1) % EnumFacing.VALUES.length]; }
            else if (limit == 1) { f = player.isSneaking() ? f.rotateAround(side.getAxis()).getOpposite() : f.rotateAround(side.getAxis()); }
            else if (limit == 2 || limit == 5) { f = player.isSneaking() ? f.rotateYCCW() : f.rotateY(); }
            ((IDirectionalTile)tile).setFacing(f);
            ((IDirectionalTile)tile).afterRotation(oldF, f);
            tile.markDirty();
            world.notifyBlockUpdate(pos, state, state, 3);
            world.addBlockEvent(tile.getPos(), tile.getBlockType(), 255, 0);
            return true;
        }
        if (tile instanceof IHammerInteraction && ICUtils.isHammer(heldItem) && !world.isRemote) {
            boolean b = ((IHammerInteraction)tile).hammerUseSide(side, player, hitX, hitY, hitZ);
            if (b) { return true; }
        }
        if (tile instanceof IPlayerInteraction) {
            boolean b = ((IPlayerInteraction)tile).interact(side, player, hand, heldItem, hitX, hitY, hitZ);
            if (b) { return true; }
        }
        if (tile instanceof IGuiTile && hand == EnumHand.MAIN_HAND && !player.isSneaking()) {
            TileEntity master = ((IGuiTile)tile).getGuiMaster();
            if (!world.isRemote && master != null && ((IGuiTile)master).canOpenGui(player)) { context.guiOpener.open(player, (TileEntity & IGuiTile)master); }
            return true;
        }
        return false;
    }

    @Override public void neighborChanged(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block block, @Nonnull BlockPos fromPos) {
        if (!world.isRemote) {
            Chunk posChunk = world.getChunk(pos);
            ICUtils.addFutureServerTask(world, () -> {
                if (world.isBlockLoaded(pos) && !posChunk.unloadQueued) {
                    TileEntity tile = world.getTileEntity(pos);
                    if (tile instanceof INeighbourChangeTile && !tile.getWorld().isRemote) { ((INeighbourChangeTile)tile).onNeighborBlockChange(fromPos); }
                }
            });
        }
    }

    @Override public int getLightValue(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof ILightValue) { return ((ILightValue)te).getLightValue(); }
        return 0;
    }

    @Override @Nonnull public AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        if (world.getBlockState(pos).getBlock() != this) { return FULL_BLOCK_AABB; }
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof IBlockBounds) {
            float[] bounds = ((IBlockBounds)te).getBlockBounds();
            return new AxisAlignedBB(bounds[0], bounds[1], bounds[2], bounds[3], bounds[4], bounds[5]);
        }
        return super.getBoundingBox(state, world, pos);
    }

    @SideOnly(Side.CLIENT)
    @Override @Nonnull public AxisAlignedBB getSelectedBoundingBox(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof ISelectionBounds) {
            List<AxisAlignedBB> list = ((ISelectionBounds)te).getAdvancedSelectionBounds();
            if (!list.isEmpty()) { return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D); }
        }
        return getBoundingBox(state, world, pos).offset(pos);
    }

    @Override public void addCollisionBoxToList(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB mask, @Nonnull List<AxisAlignedBB> list, @Nullable Entity ent, boolean isActualState) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof ICollisionBounds) {
            List<AxisAlignedBB> bounds = ((ICollisionBounds)te).getAdvancedCollisionBounds();
            if (!bounds.isEmpty()) {
                for (AxisAlignedBB aabb : bounds) {
                    AxisAlignedBB worldAABB = aabb.offset(pos);
                    if (worldAABB.intersects(mask)) { list.add(worldAABB); }
                }
                return;
            }
        }
        super.addCollisionBoxToList(state, world, pos, mask, list, ent, isActualState);
    }

    @Override public RayTraceResult collisionRayTrace(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Vec3d start, @Nonnull Vec3d end) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof ISelectionBounds) {
            RayTraceResult minMOP = null;
            double minDist = Double.POSITIVE_INFINITY;
            int subHit = 0;
            for (AxisAlignedBB aabb : ((ISelectionBounds)te).getAdvancedSelectionBounds()) {
                RayTraceResult mop = aabb.offset(pos).calculateIntercept(start, end);
                if (mop != null) {
                    mop = new RayTraceResult(mop.hitVec, mop.sideHit, pos);
                    double dist = mop.hitVec.squareDistanceTo(start);
                    if (dist < minDist) {
                        minMOP = mop;
                        minMOP.subHit = subHit;
                        minDist = dist;
                    }
                }
                subHit++;
            }
            if (minMOP != null) { return minMOP; }
        }
        return super.collisionRayTrace(state, world, pos, start, end);
    }

    @Override public boolean hasComparatorInputOverride(@Nonnull IBlockState state) { return true; }

    @Override public int getComparatorInputOverride(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof ICBlockInterfaces.IComparatorOverride) { return ((ICBlockInterfaces.IComparatorOverride)te).getComparatorInputOverride(); }
        return 0;
    }

    @Override public int getWeakPower(@Nonnull IBlockState blockState, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof ICBlockInterfaces.IRedstoneOutput) { return ((ICBlockInterfaces.IRedstoneOutput)te).getWeakRSOutput(blockState, side); }
        return 0;
    }

    @Override public int getStrongPower(@Nonnull IBlockState blockState, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof ICBlockInterfaces.IRedstoneOutput) { return ((ICBlockInterfaces.IRedstoneOutput)te).getStrongRSOutput(blockState, side); }
        return 0;
    }

    @Override public boolean canProvidePower(@Nonnull IBlockState state) { return true; }

    @Override public boolean canConnectRedstone(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable EnumFacing side) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof ICTileEntityMultiblockMetal<?, ?>) {
            ICTileEntityMultiblockMetal<?, ?> multiblockTE = (ICTileEntityMultiblockMetal<?, ?>)te;
            for (int tePos : multiblockTE.getRedstonePos()) { if (tePos == multiblockTE.pos) { return true; } }
        }
        if (te instanceof ICBlockInterfaces.IRedstoneOutput) {
            assert side != null;
            return ((ICBlockInterfaces.IRedstoneOutput)te).canConnectRedstone(state, side); }
        return false;
    }

    @Override public void onEntityCollision(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Entity entity) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null && ICMods.immersiveEngineering()) { IETileBridge.onEntityCollision(te, world, entity); }
    }
}
