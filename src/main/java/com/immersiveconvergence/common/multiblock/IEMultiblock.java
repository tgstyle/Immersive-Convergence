package com.immersiveconvergence.common.multiblock;

import com.immersiveconvergence.api.multiblock.LocalFacing;
import com.immersiveconvergence.api.multiblock.MultiblockShapes;
import com.immersiveconvergence.api.multiblock.PoIJSONSchema;
import com.immersiveconvergence.api.multiblock.ShapeData;
import com.immersiveconvergence.api.multiblock.TemplateMultiblock;
import com.immersiveconvergence.api.shapes.Shapes;

import blusunrize.immersiveengineering.api.MultiblockHandler;
import blusunrize.immersiveengineering.api.tool.ConveyorHandler;
import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityConveyorBelt;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class IEMultiblock extends TemplateMultiblock {
    public enum Anchor { SIDE, SIDE_RAW, YAW_OPPOSITE, CONVEYOR_ROW, MANUAL_ONLY }

    public interface PostFormation { void afterForming(World world, BlockPos clicked, EnumFacing facing, EntityPlayer player); }

    private static final String CONVEYOR = "conveyor";
    private static final String CONVEYOR_FACING = "conveyorFacing";
    private final Supplier<IBlockState> blockState;
    private final Supplier<IBlockState> masterState;
    private final Anchor anchor;
    private final boolean mirrorable;
    private final PostFormation postFormation;
    private final ShapeData shape;
    private final PoIJSONSchema[] pointsOfInterest;
    private final Map<String, int[]> namedPositions = new HashMap<>();
    private final Map<Integer, List<AxisAlignedBB>> boundsCache = new ConcurrentHashMap<>();
    private volatile Map<Integer, Integer> ports;
    private EnumFacing formedFacing;

    public IEMultiblock(String uniqueName, ShapeData shape, Supplier<IBlockState> blockState, Supplier<IBlockState> masterState, Anchor anchor, boolean mirrorable, PostFormation postFormation) {
        super(uniqueName, shape);
        this.blockState = blockState;
        this.masterState = masterState;
        this.anchor = anchor;
        this.mirrorable = mirrorable;
        this.postFormation = postFormation;
        this.shape = shape;
        this.pointsOfInterest = shape.data != null && shape.data.pointsOfInterest != null ? shape.data.pointsOfInterest : new PoIJSONSchema[0];
    }

    public List<AxisAlignedBB> boundsFor(int position, EnumFacing facing, boolean mirrored) {
        int key = (position * 6 + facing.ordinal()) * 2 + (mirrored ? 1 : 0);
        List<AxisAlignedBB> cached = boundsCache.get(key);
        if (cached != null) { return cached; }
        List<AxisAlignedBB> solid = new ArrayList<>();
        List<AxisAlignedBB> planes = new ArrayList<>();
        for (AxisAlignedBB aabb : shape.getShape(MultiblockShapes.localPos(position, template.width, template.length))) {
            if (isPlane(aabb)) { planes.add(aabb); }
            else { solid.add(aabb); }
        }
        List<AxisAlignedBB> bounds = new ArrayList<>(MultiblockShapes.rotated(solid, facing, mirrored).toAabbs());
        for (AxisAlignedBB plane : planes) { bounds.add(Shapes.rotateAABB(plane, facing, mirrored)); }
        boundsCache.put(key, bounds);
        return bounds;
    }

    private static boolean isPlane(AxisAlignedBB aabb) {
        int flat = 0;
        if (aabb.maxX - aabb.minX < 1.0E-7D) { flat++; }
        if (aabb.maxY - aabb.minY < 1.0E-7D) { flat++; }
        if (aabb.maxZ - aabb.minZ < 1.0E-7D) { flat++; }
        return flat == 1;
    }

    public List<AxisAlignedBB> boundsFor(int position, EnumFacing facing, boolean mirrored, BlockPos origin) {
        List<AxisAlignedBB> local = boundsFor(position, facing, mirrored);
        List<AxisAlignedBB> offset = new ArrayList<>(local.size());
        for (AxisAlignedBB aabb : local) { offset.add(aabb.offset(origin.getX(), origin.getY(), origin.getZ())); }
        return offset;
    }

    public float[] blockBoundsFor(int position, EnumFacing facing, boolean mirrored) {
        List<AxisAlignedBB> bounds = boundsFor(position, facing, mirrored);
        if (bounds.isEmpty()) { return new float[6]; }
        AxisAlignedBB union = bounds.get(0);
        for (AxisAlignedBB aabb : bounds) { union = union.union(aabb); }
        return new float[]{(float)union.minX, (float)union.minY, (float)union.minZ, (float)union.maxX, (float)union.maxY, (float)union.maxZ};
    }

    public int portPos(int position) {
        if (ports == null) { immersiveconvergence$buildPorts(); }
        Integer canonical = ports.get(position);
        if (canonical != null) { return canonical; }
        return ports.containsValue(position) ? -1 : position;
    }

    private synchronized void immersiveconvergence$buildPorts() {
        if (ports != null) { return; }
        Map<Integer, Integer> built = new HashMap<>();
        for (PoIJSONSchema poi : pointsOfInterest) {
            if (poi.name == null || poi.position == null || !poi.name.startsWith("port")) { continue; }
            int canonical = Integer.parseInt(poi.name.substring(4));
            built.put(poi.position.getY() * (template.width * template.length) + poi.position.getZ() * template.width + poi.position.getX(), canonical);
        }
        ports = built;
    }

    public int[] positionsNamed(String prefix) {
        if (namedPositions.containsKey(prefix)) { return namedPositions.get(prefix); }
        List<Integer> found = new ArrayList<>();
        for (PoIJSONSchema poi : pointsOfInterest) {
            if (poi.name == null || poi.position == null || !poi.name.startsWith(prefix)) { continue; }
            found.add(poi.position.getY() * (template.width * template.length) + poi.position.getZ() * template.width + poi.position.getX());
        }
        int[] positions = null;
        if (!found.isEmpty()) {
            positions = new int[found.size()];
            for (int i = 0; i < positions.length; i++) { positions[i] = found.get(i); }
        }
        namedPositions.put(prefix, positions);
        return positions;
    }

    public boolean formable() { return anchor != Anchor.MANUAL_ONLY; }

    public boolean hasShape() { return shape.hasShape; }

    @Override @SideOnly(Side.CLIENT) public boolean overwriteBlockRender(ItemStack stack, int iterator) { return false; }

    @Override @SideOnly(Side.CLIENT) public boolean canRenderFormedStructure() { return false; }

    @Override @SideOnly(Side.CLIENT) public void renderFormedStructure() { }

    @Override protected EnumFacing facingFor(World world, BlockPos pos, EnumFacing side, EntityPlayer player) {
        EnumFacing yaw = EnumFacing.fromAngle(player.rotationYaw);
        if (anchor == Anchor.YAW_OPPOSITE) { return yaw.getOpposite(); }
        boolean vertical = side.getAxis() == EnumFacing.Axis.Y;
        switch (anchor) {
            case SIDE_RAW: return vertical ? yaw : side;
            case CONVEYOR_ROW: {
                EnumFacing row = (vertical ? yaw : side).rotateY();
                TileEntity tile = world.getTileEntity(pos.offset(row));
                return tile instanceof TileEntityConveyorBelt ? ((TileEntityConveyorBelt)tile).getFacing() : row;
            }
            default: return vertical ? yaw : side.getOpposite();
        }
    }

    @Override protected boolean canMirror() { return mirrorable; }

    @Override protected int localX(int x, boolean mirrored) { return mirrored ? -x : x; }

    @Override protected boolean cellMatches(World world, BlockPos worldPos, int x, int y, int z, IBlockState expected, EnumFacing side, boolean mirror) {
        NBTTagCompound data = template.getCellData(x, y, z);
        if (data == null || !data.hasKey(CONVEYOR)) { return super.cellMatches(world, worldPos, x, y, z, expected, side, mirror); }
        return ConveyorHandler.isConveyor(world, worldPos, data.getString(CONVEYOR), conveyorFacing(data, side, mirror));
    }

    @Override protected ItemStack stackFor(int x, int y, int z, IBlockState state) {
        NBTTagCompound data = template.getCellData(x, y, z);
        if (data == null || !data.hasKey(CONVEYOR)) { return super.stackFor(x, y, z, state); }
        return ConveyorHandler.getConveyorStack(data.getString(CONVEYOR));
    }

    @Override protected boolean isInvalid(World world, BlockPos pos, EnumFacing side, BlockPos trigger, boolean mirror) {
        if (super.isInvalid(world, pos, side, trigger, mirror)) { return true; }
        if (template.airCells.isEmpty()) { return false; }
        BlockPos origin = originFor(pos, side, trigger, mirror);
        for (BlockPos cell : template.airCells) {
            if (!world.isAirBlock(localToWorld(origin, localX(cell.getX(), mirror), cell.getY(), cell.getZ(), side))) { return true; }
        }
        return false;
    }

    @Override protected boolean allowFormation(EntityPlayer player, BlockPos pos, ItemStack hammer) { return !MultiblockHandler.fireMultiblockFormationEventPost(player, this, pos, hammer).isCanceled(); }

    @Override protected void onFormed(EntityPlayer player, BlockPos pos, ItemStack hammer) {
        if (postFormation != null) { postFormation.afterForming(player.world, pos, formedFacing, player); }
    }

    @Override protected void replaceStructureBlock(World world, BlockPos worldPos, BlockPos masterWorldPos, int position, boolean mirrored, EnumFacing side) {
        formedFacing = side;
        Supplier<IBlockState> chosen = (masterState != null && worldPos.equals(masterWorldPos)) ? masterState : blockState;
        IBlockState state = chosen.get();
        world.setBlockState(worldPos, state);
        TileEntity tile = world.getTileEntity(worldPos);
        if (tile instanceof TileEntityMultiblockPart) {
            TileEntityMultiblockPart<?> part = (TileEntityMultiblockPart<?>)tile;
            part.facing = side;
            part.formed = true;
            part.pos = position;
            part.offset = new int[]{worldPos.getX() - masterWorldPos.getX(), worldPos.getY() - masterWorldPos.getY(), worldPos.getZ() - masterWorldPos.getZ()};
            part.mirrored = mirrored;
            part.markDirty();
            world.addBlockEvent(worldPos, state.getBlock(), 255, 0);
        }
    }

    private EnumFacing conveyorFacing(NBTTagCompound data, EnumFacing side, boolean mirror) {
        if (!data.hasKey(CONVEYOR_FACING)) { return null; }
        LocalFacing local = LocalFacing.valueOf(data.getString(CONVEYOR_FACING).toUpperCase(Locale.ENGLISH));
        if (mirror && local == LocalFacing.LEFT) { local = LocalFacing.RIGHT; }
        else if (mirror && local == LocalFacing.RIGHT) { local = LocalFacing.LEFT; }
        return local.LocalToGlobal(side);
    }
}
