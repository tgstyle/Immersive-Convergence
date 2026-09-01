package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.common.util.ICLogger;
import com.immersiveconvergence.common.util.ICResources;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public final class TemplateData {
    private static final HashMap<String, TemplateData> CACHE = new HashMap<>();
    public final int width, height, length;
    public final IBlockState[][][] structure;
    public final Set<BlockPos> airCells;
    public final HashMap<BlockPos, NBTTagCompound> cellData;

    private TemplateData(int width, int height, int length, IBlockState[][][] structure, Set<BlockPos> airCells, HashMap<BlockPos, NBTTagCompound> cellData) {
        this.width = width;
        this.height = height;
        this.length = length;
        this.structure = structure;
        this.airCells = airCells;
        this.cellData = cellData;
    }

    public static TemplateData load(String modid, String id) {
        String key = modid + ":" + id;
        if (CACHE.containsKey(key)) { return CACHE.get(key); }
        TemplateData data = null;
        try (InputStream stream = ICResources.open(modid, "structures/multiblocks/" + id + ".nbt")) {
            data = parse(CompressedStreamTools.readCompressed(stream));
        } catch (Exception e) { ICLogger.error("Couldn't load structure " + key + ": " + e); }
        CACHE.put(key, data);
        return data;
    }

    private static TemplateData parse(NBTTagCompound nbt) {
        NBTTagList size = nbt.getTagList("size", 3);
        int width = size.getIntAt(0), height = size.getIntAt(1), length = size.getIntAt(2);
        NBTTagList paletteTag = nbt.getTagList("palette", 10);
        IBlockState[] palette = new IBlockState[paletteTag.tagCount()];
        for (int i = 0; i < palette.length; i++) { palette[i] = NBTUtil.readBlockState(paletteTag.getCompoundTagAt(i)); }
        IBlockState[][][] structure = new IBlockState[height][length][width];
        Set<BlockPos> airCells = new HashSet<>();
        HashMap<BlockPos, NBTTagCompound> cellData = new HashMap<>();
        NBTTagList blocks = nbt.getTagList("blocks", 10);
        for (int i = 0; i < blocks.tagCount(); i++) {
            NBTTagCompound blockTag = blocks.getCompoundTagAt(i);
            IBlockState state = palette[blockTag.getInteger("state")];
            NBTTagList pos = blockTag.getTagList("pos", 3);
            if (blockTag.hasKey("nbt", 10)) { cellData.put(new BlockPos(pos.getIntAt(0), pos.getIntAt(1), pos.getIntAt(2)), blockTag.getCompoundTag("nbt")); }
            if (state.getBlock() == Blocks.AIR) { airCells.add(new BlockPos(pos.getIntAt(0), pos.getIntAt(1), pos.getIntAt(2))); }
            if (state.getBlock() == Blocks.AIR || state.getBlock() == Blocks.STRUCTURE_VOID) { continue; }
            structure[pos.getIntAt(1)][pos.getIntAt(2)][pos.getIntAt(0)] = state;
        }
        return new TemplateData(width, height, length, structure, airCells, cellData);
    }

    public NBTTagCompound getCellData(int x, int y, int z) { return cellData.get(new BlockPos(x, y, z)); }

    public IBlockState getState(int x, int y, int z) { return structure[y][z][x]; }

    public IBlockState getState(int position) {
        if (position < 0 || position >= width * height * length) { return null; }
        return structure[position / (width * length)][position % (width * length) / width][position % width];
    }
}
