package com.immersiveconvergence.core.compat.opencomputers.ie;

import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackOwner;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callbacks;
import li.cil.oc.api.driver.DriverBlock;
import li.cil.oc.api.network.ManagedEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class IEBlockDriver implements DriverBlock {
    private final Map<ResourceLocation, CallbackOwner<?>> owners = new HashMap<>();
    private final Map<String, String> names;

    public IEBlockDriver(Map<String, String> names) {
        this.names = names;
        for (Map.Entry<ResourceLocation, CallbackOwner<?>> entry : Callbacks.getCallbacks().entrySet()) {
            if (names.containsKey(entry.getValue().getName())) { owners.put(entry.getKey(), entry.getValue()); }
        }
    }

    @Override public boolean worksWith(Level level, BlockPos pos, Direction side) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) { return false; }
        CallbackOwner<?> owner = owners.get(ForgeRegistries.BLOCK_ENTITY_TYPES.getKey(blockEntity.getType()));
        return owner != null && accepts(owner, blockEntity);
    }

    @Override public ManagedEnvironment createEnvironment(Level level, BlockPos pos, Direction side) {
        BlockEntity blockEntity = Objects.requireNonNull(level.getBlockEntity(pos));
        CallbackOwner<?> owner = owners.get(ForgeRegistries.BLOCK_ENTITY_TYPES.getKey(blockEntity.getType()));
        return environment(owner, blockEntity, level);
    }

    private static <T> boolean accepts(CallbackOwner<T> owner, BlockEntity blockEntity) { return owner.getCallbackType().isInstance(blockEntity) && owner.canAttachTo(owner.getCallbackType().cast(blockEntity)); }

    private <T> ManagedEnvironment environment(CallbackOwner<T> owner, BlockEntity blockEntity, Level level) { return new IEEnvironment<>(owner, owner.getCallbackType().cast(blockEntity), blockEntity, level, names.get(owner.getName())); }
}
