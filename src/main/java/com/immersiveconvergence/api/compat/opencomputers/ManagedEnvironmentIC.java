package com.immersiveconvergence.api.compat.opencomputers;

import com.immersiveconvergence.api.block.ICTileEntityBase;
import com.immersiveconvergence.api.compat.ICComputerControl;
import com.immersiveconvergence.api.compat.ICComputerTable;
import com.immersiveconvergence.api.multiblock.ICTileEntityMultiblockMetal;

import li.cil.oc.api.Network;
import li.cil.oc.api.driver.NamedBlock;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.AbstractManagedEnvironment;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@SuppressWarnings("unused")
public abstract class ManagedEnvironmentIC<T extends ICTileEntityBase> extends AbstractManagedEnvironment implements NamedBlock {
    final World world;
    final BlockPos pos;
    final Class<? extends ICTileEntityBase> tileClass;

    public ManagedEnvironmentIC(World world, BlockPos pos, Class<? extends ICTileEntityBase> tileClass) {
        this.world = world;
        this.pos = pos;
        this.tileClass = tileClass;
        setNode(Network.newNode(this, Visibility.Network).withComponent(preferredName(), Visibility.Network).create());
    }

    @SuppressWarnings("unchecked")
    protected T getTileEntity() {
        TileEntity te = world.getTileEntity(pos);
        if (te != null && tileClass.isAssignableFrom(te.getClass())) { return (T)te; }
        return null;
    }

    public abstract static class ManagedEnvMultiblock<T2 extends ICTileEntityMultiblockMetal<?, ?>> extends ManagedEnvironmentIC<T2> {
        public ManagedEnvMultiblock(World world, BlockPos pos, Class<? extends ICTileEntityBase> tileClass) { super(world, pos, tileClass); }

        protected Object[] call(ICComputerTable<T2> table, String method) { return table.invoke(getTileEntity(), method); }

        protected Object[] enableComputerControl(Context context, Arguments args) {
            boolean allow = args.checkBoolean(0);
            ICComputerControl.enableComputerControl(getTileEntity(), allow);
            return null;
        }

        protected Object[] setEnabled(Context context, Arguments args) {
            boolean enabled = args.checkBoolean(0);
            ICComputerControl.setEnabled(getTileEntity(), enabled);
            return null;
        }
    }
}
