package com.immersiveconvergence.api.integration.jade;

import com.immersiveconvergence.api.integration.DisplayContexts;
import com.immersiveconvergence.api.multiblock.IDisplayContext;
import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import snownee.jade.api.Accessor;
import snownee.jade.api.fluid.JadeFluidObject;
import snownee.jade.api.view.ClientViewGroup;
import snownee.jade.api.view.FluidView;
import snownee.jade.api.view.IClientExtensionProvider;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ViewGroup;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public enum JadeFluidProvider implements IServerExtensionProvider<Object, CompoundTag>, IClientExtensionProvider<CompoundTag, FluidView> {
    INSTANCE;

    @Override @Nullable public List<ViewGroup<CompoundTag>> getGroups(ServerPlayer player, ServerLevel level, Object target, boolean showDetails) {
        IDisplayContext context = target instanceof BlockEntity be ? DisplayContexts.of(be) : null;
        if (context == null) { return null; }
        List<CompoundTag> list = new ArrayList<>();
        for (IFluidTank tank : context.getInternalTanks()) {
            if (tank == null) { continue; }
            FluidStack fs = tank.getFluid();
            if (fs.isEmpty()) { continue; }
            list.add(FluidView.writeDefault(JadeFluidObject.of(fs.getFluid(), fs.getAmount(), fs.getTag()), tank.getCapacity()));
        }
        return list.isEmpty() ? null : List.of(new ViewGroup<>(list));
    }

    @Override public List<ClientViewGroup<FluidView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<CompoundTag>> list) { return ClientViewGroup.map(list, FluidView::readDefault, null); }

    @Override public ResourceLocation getUid() { return ICLib.rl("fluid"); }
}
