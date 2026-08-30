package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.common.util.ICLogger;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;

public final class TemplateData {
    private static final HashMap<String, TemplateData> CACHE = new HashMap<>();
    public final int width, height, length;
    public final IBlockState[][][] structure;

    private TemplateData(int width, int height, int length, IBlockState[][][] structure) {
        this.width = width;
        this.height = height;
        this.length = length;
        this.structure = structure;
    }

    public static TemplateData load(String modid, String id) {
        String key = modid + ":" + id;
        if (CACHE.containsKey(key)) { return CACHE.get(key); }
        TemplateData data = null;
        try {
            InputStream stream = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream(String.format("assets/%s/structures/multiblocks/%s.nbt", modid, id)));
            NBTTagCompound nbt = CompressedStreamTools.readCompressed(stream);
            stream.close();
            data = parse(nbt);
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
        NBTTagList blocks = nbt.getTagList("blocks", 10);
        for (int i = 0; i < blocks.tagCount(); i++) {
            NBTTagCompound blockTag = blocks.getCompoundTagAt(i);
            IBlockState state = palette[blockTag.getInteger("state")];
            if (state.getBlock() == Blocks.AIR || state.getBlock() == Blocks.STRUCTURE_VOID) { continue; }
            NBTTagList pos = blockTag.getTagList("pos", 3);
            structure[pos.getIntAt(1)][pos.getIntAt(2)][pos.getIntAt(0)] = state;
        }
        return new TemplateData(width, height, length, structure);
    }

    public IBlockState getState(int x, int y, int z) { return structure[y][z][x]; }

    public IBlockState getState(int position) {
        if (position < 0 || position >= width * height * length) { return null; }
        return structure[position / (width * length)][position % (width * length) / width][position % width];
    }
}
