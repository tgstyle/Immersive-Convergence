package com.immersiveconvergence.api.integration.jade;

import com.immersiveconvergence.api.integration.DisplayContexts;
import com.immersiveconvergence.api.multiblock.IDisplayContext;
import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.energy.IEnergyStorage;
import snownee.jade.api.Accessor;
import snownee.jade.api.view.ClientViewGroup;
import snownee.jade.api.view.EnergyView;
import snownee.jade.api.view.IClientExtensionProvider;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ViewGroup;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public enum JadeEnergyProvider implements IServerExtensionProvider<CompoundTag>, IClientExtensionProvider<CompoundTag, EnergyView> {
    INSTANCE;

    @Override @Nullable public List<ViewGroup<CompoundTag>> getGroups(Accessor<?> accessor) {
        IDisplayContext context = accessor.getTarget() instanceof BlockEntity be ? DisplayContexts.of(be) : null;
        if (context == null) { return null; }
        List<IEnergyStorage> energies = context.getEnergies();
        if (energies.isEmpty()) { return null; }
        List<CompoundTag> list = new ArrayList<>();
        for (IEnergyStorage energy : energies) { list.add(EnergyView.of(energy.getEnergyStored(), energy.getMaxEnergyStored())); }
        return List.of(new ViewGroup<>(list));
    }

    @Override public List<ClientViewGroup<EnergyView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<CompoundTag>> list) { return ClientViewGroup.map(list, tag -> EnergyView.read(tag, "IF"), null); }

    @Override public ResourceLocation getUid() { return ICLib.rl("energy"); }
}
