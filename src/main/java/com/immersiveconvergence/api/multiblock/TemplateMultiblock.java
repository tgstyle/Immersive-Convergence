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
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public abstract class TemplateMultiblock implements MultiblockHandler.IMultiblock {
    public final String uniqueName;
    public final TemplateData template;
    public final BlockPos masterPos, triggerPos;
    public final float manualScale;
    private ItemStack[][][] structureManual;
    private IngredientStack[] materials;

    protected TemplateMultiblock(String uniqueName, ShapeData shape) {
        this.uniqueName = uniqueName;
        this.template = shape.template;
        this.masterPos = shape.masterPos;
        this.triggerPos = shape.triggerPos;
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
        return BlockMatcher.matches(templateState(triggerPos.getX(), triggerPos.getY(), triggerPos.getZ()), state);
    }

    @Override public boolean createStructure(World world, BlockPos pos, EnumFacing side, EntityPlayer player) {
        if (template == null) { return false; }
        side = (side == EnumFacing.UP || side == EnumFacing.DOWN) ? EnumFacing.fromAngle(player.rotationYaw) : side.getOpposite();
        boolean mirror = false;
        if (isInvalid(world, pos, side, false)) {
            mirror = true;
            if (isInvalid(world, pos, side, true)) { return false; }
        }
        int width = template.width, height = template.height, length = template.length;
        BlockPos origin = pos.offset(side, -triggerPos.getZ()).offset(side.rotateY(), mirror ? -(width - 1 - triggerPos.getX()) : -triggerPos.getX()).offset(EnumFacing.DOWN, triggerPos.getY());
        BlockPos masterWorldPos = localToWorld(origin, mirror ? (width - 1 - masterPos.getX()) : masterPos.getX(), masterPos.getY(), masterPos.getZ(), side);
        ItemStack hammer = player.getHeldItemMainhand().getItem().getToolClasses(player.getHeldItemMainhand()).contains(Lib.TOOL_HAMMER) ? player.getHeldItemMainhand() : player.getHeldItemOffhand();
        if (MultiblockHandler.fireMultiblockFormationEventPre(player, this, pos, hammer).isCanceled()) { return false; }
        for (int h = 0; h < height; h++) {
            for (int l = 0; l < length; l++) {
                for (int w = 0; w < width; w++) {
                    if (templateState(w, h, l) == null) { continue; }
                    int position = h * (width * length) + l * width + w;
                    BlockPos worldPos = localToWorld(origin, mirror ? (width - 1 - w) : w, h, l, side);
                    replaceStructureBlock(world, worldPos, masterWorldPos, position, mirror, side);
                }
            }
        }
        MultiblockHandler.fireMultiblockFormationEventPost(player, this, pos, hammer);
        return true;
    }

    protected boolean isInvalid(World world, BlockPos pos, EnumFacing side, boolean mirror) {
        int width = template.width, height = template.height, length = template.length;
        BlockPos origin = pos.offset(side, -triggerPos.getZ()).offset(side.rotateY(), mirror ? -(width - 1 - triggerPos.getX()) : -triggerPos.getX()).offset(EnumFacing.DOWN, triggerPos.getY());
        for (int h = 0; h < height; h++) {
            for (int l = 0; l < length; l++) {
                for (int w = 0; w < width; w++) {
                    IBlockState expected = templateState(w, h, l);
                    if (expected == null) { continue; }
                    BlockPos blockPos = localToWorld(origin, mirror ? (width - 1 - w) : w, h, l, side);
                    if (!BlockMatcher.matches(expected, world.getBlockState(blockPos))) { return true; }
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
                        structureManual[h][l][w] = state == null ? ItemStack.EMPTY : BlockMatcher.stackFromState(state);
                    }
                }
            }
        }
        return structureManual;
    }

    @Override public IngredientStack[] getTotalMaterials() {
        if (materials == null && template != null) {
            LinkedHashMap<IBlockState, Integer> counts = new LinkedHashMap<>();
            for (int h = 0; h < template.height; h++) {
                for (int l = 0; l < template.length; l++) {
                    for (int w = 0; w < template.width; w++) {
                        IBlockState state = templateState(w, h, l);
                        if (state != null) { counts.merge(state, 1, Integer::sum); }
                    }
                }
            }
            ArrayList<IngredientStack> ingredients = new ArrayList<>();
            for (Map.Entry<IBlockState, Integer> entry : counts.entrySet()) {
                ItemStack stack = BlockMatcher.stackFromState(entry.getKey());
                if (stack.isEmpty()) { continue; }
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
        IBlockState state = template == null ? null : templateState(position);
        return state == null ? ItemStack.EMPTY : BlockMatcher.stackFromState(state);
    }

    public Set<BlockPos> worldOffsetsFromMaster(EnumFacing facing, boolean mirrored) {
        Set<BlockPos> offsets = new HashSet<>();
        if (template == null) { return offsets; }
        int width = template.width;
        int masterX = mirrored ? width - 1 - masterPos.getX() : masterPos.getX();
        BlockPos master = localToWorld(BlockPos.ORIGIN, masterX, masterPos.getY(), masterPos.getZ(), facing);
        for (int h = 0; h < template.height; h++) {
            for (int l = 0; l < template.length; l++) {
                for (int w = 0; w < width; w++) {
                    if (templateState(w, h, l) == null) { continue; }
                    offsets.add(localToWorld(BlockPos.ORIGIN, mirrored ? width - 1 - w : w, h, l, facing).subtract(master));
                }
            }
        }
        return offsets;
    }

    protected static BlockPos localToWorld(BlockPos origin, int x, int y, int z, EnumFacing facing) {
        switch (facing) {
            case SOUTH: return origin.add(-x, y, z);
            case NORTH: return origin.add(x, y, -z);
            case EAST: return origin.add(z, y, x);
            case WEST: return origin.add(-z, y, -x);
            default: return origin;
        }
    }
}
