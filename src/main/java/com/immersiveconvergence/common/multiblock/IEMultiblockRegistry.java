package com.immersiveconvergence.common.multiblock;

import com.immersiveconvergence.api.multiblock.ShapeData;

import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class IEMultiblockRegistry {
    public static final String MODID = "immersiveengineering";
    private static final Map<String, Definition> definitions = new LinkedHashMap<>();
    private static final Map<String, IEMultiblock> loaded = new HashMap<>();
    private static final Map<Class<?>, String> tileNames = new HashMap<>();

    private IEMultiblockRegistry() {}

    public static void register(String uniqueName, String id, Supplier<IBlockState> blockState, IEMultiblock.Anchor anchor, boolean mirrorable) { register(uniqueName, id, blockState, anchor, mirrorable, null); }

    public static void register(String uniqueName, String id, Supplier<IBlockState> blockState, IEMultiblock.Anchor anchor, boolean mirrorable, IEMultiblock.PostFormation postFormation) { definitions.put(uniqueName, new Definition(id, blockState, anchor, mirrorable, postFormation)); }

    public static IEMultiblock get(String uniqueName) {
        if (loaded.containsKey(uniqueName)) { return loaded.get(uniqueName); }
        Definition definition = definitions.get(uniqueName);
        IEMultiblock multiblock = null;
        if (definition != null) {
            ShapeData shape = ShapeData.load(MODID, definition.id);
            if (shape.template != null) { multiblock = new IEMultiblock(uniqueName, shape, definition.blockState, definition.anchor, definition.mirrorable, definition.postFormation); }
        }
        loaded.put(uniqueName, multiblock);
        return multiblock;
    }

    public static void loadAll() { for (String uniqueName : definitions.keySet()) { get(uniqueName); } }

    public static void registerTile(Class<?> tile, String uniqueName) { tileNames.put(tile, uniqueName); }

    public static IEMultiblock templateFor(TileEntityMultiblockPart<?> part) {
        String name = tileNames.get(part.getClass());
        return name == null ? null : get(name);
    }

    public static ItemStack getOriginalBlock(String uniqueName, int position) {
        IEMultiblock multiblock = get(uniqueName);
        return multiblock == null ? null : multiblock.getOriginalBlock(position);
    }

    private static final class Definition {
        private final String id;
        private final Supplier<IBlockState> blockState;
        private final IEMultiblock.Anchor anchor;
        private final boolean mirrorable;
        private final IEMultiblock.PostFormation postFormation;

        private Definition(String id, Supplier<IBlockState> blockState, IEMultiblock.Anchor anchor, boolean mirrorable, IEMultiblock.PostFormation postFormation) {
            this.id = id;
            this.blockState = blockState;
            this.anchor = anchor;
            this.mirrorable = mirrorable;
            this.postFormation = postFormation;
        }
    }
}
