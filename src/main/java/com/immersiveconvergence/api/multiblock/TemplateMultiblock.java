package com.immersiveconvergence.api.multiblock;

import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.api.MultiblockHandler;
import blusunrize.immersiveengineering.api.crafting.IngredientStack;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public abstract class TemplateMultiblock implements MultiblockHandler.IMultiblock {
    public final String uniqueName;
    public final TemplateData template;
    public final BlockPos masterPos, triggerPos;
    public final List<BlockPos> triggerPositions;
    public final List<LocalFacing> triggerFacings;
    public final float manualScale;
    private ItemStack[][][] structureManual;
    private IngredientStack[] materials;

    protected TemplateMultiblock(String uniqueName, ShapeData shape) {
        this.uniqueName = uniqueName;
        this.template = shape.template;
        this.masterPos = shape.masterPos;
        this.triggerPos = shape.triggerPos;
        this.triggerPositions = shape.triggerPositions;
        this.triggerFacings = shape.triggerFacings;
        this.manualScale = shape.manualScale;
    }

    @Override public String getUniqueName() { return uniqueName; }

    @Override public float getManualScale() { return manualScale; }

    protected IBlockState modifyTemplateState(IBlockState state) { return state; }

    protected IBlockState templateState(int x, int y, int z) {
        IBlockState state = template.getState(x, y, z);
        return state == null ? null : modifyTemplateState(state);
    }

    protected IBlockState templateState(int position) {
        IBlockState state = template.getState(position);
        return state == null ? null : modifyTemplateState(state);
    }

    @Override public boolean isBlockTrigger(IBlockState state) {
        if (template == null) { return false; }
        for (BlockPos trigger : triggerPositions) {
            if (BlockMatcher.matches(templateState(trigger.getX(), trigger.getY(), trigger.getZ()), state)) { return true; }
        }
        return false;
    }

    @Override public boolean createStructure(World world, BlockPos pos, EnumFacing side, EntityPlayer player) {
        if (template == null) { return false; }
        side = facingFor(world, pos, side, player);
        BlockPos trigger = null;
        boolean mirror = false;
        for (int i = 0; i < triggerPositions.size(); i++) {
            BlockPos candidate = triggerPositions.get(i);
            EnumFacing candidateSide = triggerFacings.get(i) == LocalFacing.BACK ? side.getOpposite() : side;
            if (!isInvalid(world, pos, candidateSide, candidate, false)) { trigger = candidate; side = candidateSide; break; }
            if (canMirror() && !isInvalid(world, pos, candidateSide, candidate, true)) {
                trigger = candidate;
                side = candidateSide;
                mirror = true;
                break;
            }
        }
        if (trigger == null) { return false; }
        int width = template.width, height = template.height, length = template.length;
        BlockPos origin = originFor(pos, side, trigger, mirror);
        BlockPos masterWorldPos = localToWorld(origin, localX(masterPos.getX(), mirror), masterPos.getY(), masterPos.getZ(), side);
        ItemStack mainhand = player.getHeldItemMainhand();
        ItemStack hammer = mainhand.getItem().getToolClasses(mainhand).contains(Lib.TOOL_HAMMER) ? mainhand : player.getHeldItemOffhand();
        if (!allowFormation(player, pos, hammer)) { return false; }
        for (int h = 0; h < height; h++) {
            for (int l = 0; l < length; l++) {
                for (int w = 0; w < width; w++) {
                    if (templateState(w, h, l) == null) { continue; }
                    int position = h * (width * length) + l * width + w;
                    BlockPos worldPos = localToWorld(origin, localX(w, mirror), h, l, side);
                    replaceStructureBlock(world, worldPos, masterWorldPos, position, mirror, side);
                }
            }
        }
        onFormed(player, pos, hammer);
        return true;
    }

    protected EnumFacing facingFor(World world, BlockPos pos, EnumFacing side, EntityPlayer player) { return (side == EnumFacing.UP || side == EnumFacing.DOWN) ? EnumFacing.fromAngle(player.rotationYaw) : side.getOpposite(); }

    protected boolean cellMatches(World world, BlockPos worldPos, int x, int y, int z, IBlockState expected, EnumFacing side, boolean mirror) { return BlockMatcher.matches(expected, world.getBlockState(worldPos)); }

    protected ItemStack stackFor(int x, int y, int z, IBlockState state) { return BlockMatcher.stackFromState(state); }

    protected boolean canMirror() { return true; }

    protected int localX(int x, boolean mirrored) { return mirrored ? template.width - 1 - x : x; }

    protected boolean allowFormation(EntityPlayer player, BlockPos pos, ItemStack hammer) { return !MultiblockHandler.fireMultiblockFormationEventPre(player, this, pos, hammer).isCanceled(); }

    protected void onFormed(EntityPlayer player, BlockPos pos, ItemStack hammer) { MultiblockHandler.fireMultiblockFormationEventPost(player, this, pos, hammer); }

    protected BlockPos originFor(BlockPos pos, EnumFacing side, BlockPos trigger, boolean mirror) {
        return pos.offset(side, -trigger.getZ()).offset(side.rotateY(), -localX(trigger.getX(), mirror)).offset(EnumFacing.DOWN, trigger.getY());
    }

    protected boolean isInvalid(World world, BlockPos pos, EnumFacing side, BlockPos trigger, boolean mirror) {
        int width = template.width, height = template.height, length = template.length;
        BlockPos origin = originFor(pos, side, trigger, mirror);
        for (int h = 0; h < height; h++) {
            for (int l = 0; l < length; l++) {
                for (int w = 0; w < width; w++) {
                    IBlockState expected = templateState(w, h, l);
                    if (expected == null) { continue; }
                    BlockPos blockPos = localToWorld(origin, localX(w, mirror), h, l, side);
                    if (!cellMatches(world, blockPos, w, h, l, expected, side, mirror)) { return true; }
                }
            }
        }
        return false;
    }

    protected abstract void replaceStructureBlock(World world, BlockPos worldPos, BlockPos masterWorldPos, int position, boolean mirrored, EnumFacing side);

    @Override public ItemStack[][][] getStructureManual() {
        if (structureManual == null && template != null) {
            structureManual = new ItemStack[template.height][template.length][template.width];
            for (int h = 0; h < template.height; h++) {
                for (int l = 0; l < template.length; l++) {
                    for (int w = 0; w < template.width; w++) {
                        IBlockState state = templateState(w, h, l);
                        structureManual[h][l][w] = state == null ? ItemStack.EMPTY : stackFor(w, h, l, state);
                    }
                }
            }
        }
        return structureManual;
    }

    @Override public IngredientStack[] getTotalMaterials() {
        if (materials == null && template != null) {
            LinkedHashMap<String, ItemStack> stacks = new LinkedHashMap<>();
            LinkedHashMap<String, Integer> counts = new LinkedHashMap<>();
            for (int h = 0; h < template.height; h++) {
                for (int l = 0; l < template.length; l++) {
                    for (int w = 0; w < template.width; w++) {
                        IBlockState state = templateState(w, h, l);
                        if (state == null) { continue; }
                        ItemStack cell = stackFor(w, h, l, state);
                        if (cell.isEmpty()) { continue; }
                        String key = cell.getItem().getRegistryName() + ":" + cell.getItemDamage() + ":" + cell.getTagCompound();
                        stacks.putIfAbsent(key, cell);
                        counts.merge(key, 1, Integer::sum);
                    }
                }
            }
            ArrayList<IngredientStack> ingredients = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                ItemStack stack = stacks.get(entry.getKey()).copy();
                String oreName = BlockMatcher.getGenericOreName(stack);
                if (oreName != null) { ingredients.add(new IngredientStack(oreName, entry.getValue())); }
                else {
                    stack.setCount(entry.getValue());
                    ingredients.add(new IngredientStack(stack));
                }
            }
            materials = ingredients.toArray(new IngredientStack[0]);
        }
        return materials;
    }

    public ItemStack getOriginalBlock(int position) {
        if (template == null || position < 0) { return ItemStack.EMPTY; }
        IBlockState state = templateState(position);
        if (state == null) { return ItemStack.EMPTY; }
        int width = template.width, length = template.length;
        return stackFor(position % width, position / (width * length), position % (width * length) / width, state);
    }

    public Set<BlockPos> worldOffsetsFromMaster(EnumFacing facing, boolean mirrored) {
        Set<BlockPos> offsets = new HashSet<>();
        if (template == null) { return offsets; }
        int width = template.width;
        BlockPos master = localToWorld(BlockPos.ORIGIN, localX(masterPos.getX(), mirrored), masterPos.getY(), masterPos.getZ(), facing);
        for (int h = 0; h < template.height; h++) {
            for (int l = 0; l < template.length; l++) {
                for (int w = 0; w < width; w++) {
                    if (templateState(w, h, l) == null) { continue; }
                    offsets.add(localToWorld(BlockPos.ORIGIN, localX(w, mirrored), h, l, facing).subtract(master));
                }
            }
        }
        return offsets;
    }

    public static BlockPos localToWorld(BlockPos origin, int x, int y, int z, EnumFacing facing, boolean mirrored) { return localToWorld(origin, mirrored ? -x : x, y, z, facing); }

    public static BlockPos localToWorld(BlockPos origin, int x, int y, int z, EnumFacing facing) {
        switch (facing) {
            case SOUTH: return origin.add(-x, y, z);
            case NORTH: return origin.add(x, y, -z);
            case EAST: return origin.add(z, y, x);
            case WEST: return origin.add(-z, y, -x);
            default: return origin;
        }
    }
}
