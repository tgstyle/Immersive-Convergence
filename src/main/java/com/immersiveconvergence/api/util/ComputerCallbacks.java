package com.immersiveconvergence.api.util;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistrationBuilder;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelper;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackOwner;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.owners.MultiblockCallbackWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class ComputerCallbacks {
    private static final Map<ResourceLocation, CallbackOwner<?>> CALLBACKS = new LinkedHashMap<>();

    private ComputerCallbacks() {}

    public static <S extends IMultiblockState> void registerMultiblock(MultiblockRegistration<S> multiblock, Callback<S> callback, String name, Predicate<BlockPos> attach) { registerMultiblock(multiblock, callback, new Callback<>(), name, attach); }

    public static <S extends IMultiblockState> void registerMultiblock(MultiblockRegistration<S> multiblock, Callback<S> callback, Callback<IMultiblockBEHelper<S>> helperCallback, String name, Predicate<BlockPos> attach) { putMultiblock(multiblock, new MultiblockComputerOwner<>(callback, helperCallback, multiblock, name, attach)); }

    public static <S extends IMultiblockState> void extendMultiblock(MultiblockRegistration<S> multiblock, Callback<S> callback, String name, BlockPos... valid) { putMultiblock(multiblock, new MultiblockCallbackWrapper<>(callback, multiblock, name, valid)); }

    private static void putMultiblock(MultiblockRegistration<?> multiblock, CallbackOwner<?> owner) {
        CALLBACKS.put(multiblock.id().withPath(path -> path + MultiblockRegistrationBuilder.MASTER_BE_SUFFIX), owner);
        CALLBACKS.put(multiblock.id().withPath(path -> path + MultiblockRegistrationBuilder.DUMMY_BE_SUFFIX), owner);
    }

    public static void register(ResourceLocation blockEntityType, CallbackOwner<?> owner) { CALLBACKS.put(blockEntityType, owner); }

    public static Predicate<BlockPos> at(Supplier<BlockPos> position) { return pos -> pos.equals(position.get()); }

    @SuppressWarnings("unused") public static Predicate<BlockPos> atAny(Supplier<Collection<BlockPos>> positions) {
        return pos -> {
            Collection<BlockPos> valid = positions.get();
            return valid != null && valid.contains(pos);
        };
    }

    public static Predicate<BlockPos> anywhere() { return pos -> true; }

    public static Map<ResourceLocation, CallbackOwner<?>> all() { return Collections.unmodifiableMap(CALLBACKS); }
}
