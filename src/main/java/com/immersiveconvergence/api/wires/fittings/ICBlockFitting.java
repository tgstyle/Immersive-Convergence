package com.immersiveconvergence.api.wires.fittings;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.block.BlockContext;
import com.immersiveconvergence.api.block.ICBlockTileProvider;
import com.immersiveconvergence.api.block.ICItemBlockBase;
import com.immersiveconvergence.api.block.ICProperties;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public abstract class ICBlockFitting extends ICBlockTileProvider<ICFittingType> {
    private final Supplier<? extends ICTileEntityConnectable> tile;

    protected ICBlockFitting(BlockContext context, String name, Supplier<? extends ICTileEntityConnectable> tile, Object... extraProperties) {
        super(context, name, Material.IRON, PropertyEnum.create("type", ICFittingType.class), ICItemBlockBase.class, properties(extraProperties));
        this.tile = tile;
        setAllNotNormalBlock();
        lightOpacity = 0;
        setHardness(3.0F);
        setResistance(15.0F);
        setHarvestLevel("pickaxe", 0);
    }

    private static Object[] properties(Object[] extra) {
        List<Object> all = new ArrayList<>(Arrays.asList(extra));
        all.add(ICProperties.FACING_ALL);
        if (!ICMods.immersiveEngineering()) { all.add(ICProperties.CONNECTIONS); }
        return all.toArray();
    }

    @Override @Nonnull protected EnumFacing getDefaultFacing() { return EnumFacing.DOWN; }

    @Override @Nonnull public TileEntity createBasicTE(World world, ICFittingType type) { return tile.get(); }
}
