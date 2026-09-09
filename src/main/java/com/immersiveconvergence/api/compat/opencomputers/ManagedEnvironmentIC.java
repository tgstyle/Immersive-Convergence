package com.immersiveconvergence.api.compat.opencomputers;

import blusunrize.immersiveengineering.common.blocks.TileEntityIEBase;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityMultiblockMetal;
import blusunrize.immersiveengineering.common.util.compat.opencomputers.ManagedEnvironmentIE;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@SuppressWarnings("unused")
public abstract class ManagedEnvironmentIC<T extends TileEntityIEBase> extends ManagedEnvironmentIE<T> {
    public ManagedEnvironmentIC(World w, BlockPos p, Class<? extends TileEntityIEBase> teClass) { super(w, p, teClass); }

    public abstract static class ManagedEnvMultiblock<T2 extends TileEntityMultiblockMetal<?, ?>> extends ManagedEnvironmentIE.ManagedEnvMultiblock<T2> {
        public ManagedEnvMultiblock(World w, BlockPos p, Class<? extends TileEntityIEBase> teClass) { super(w, p, teClass); }
    }
}
