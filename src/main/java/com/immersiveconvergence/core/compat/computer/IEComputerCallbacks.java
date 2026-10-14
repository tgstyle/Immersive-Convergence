package com.immersiveconvergence.core.compat.computer;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistrationBuilder;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelper;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackOwner;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.owners.MultiblockCallbackWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

public final class IEComputerCallbacks {
    private static final Map<ResourceLocation, CallbackOwner<?>> CALLBACKS = new LinkedHashMap<>();

    private IEComputerCallbacks() {}

    public static <S extends IMultiblockState> void registerMultiblock(MultiblockRegistration<S> multiblock, Callback<S> callback, String name, Predicate<BlockPos> attach) { registerMultiblock(multiblock, callback, new Callback<>(), name, attach); }

    public static <S extends IMultiblockState> void registerMultiblock(MultiblockRegistration<S> multiblock, Callback<S> callback, Callback<IMultiblockBEHelper<S>> helperCallback, String name, Predicate<BlockPos> attach) { putMultiblock(multiblock.id(), new MultiblockComputerOwner<>(callback, helperCallback, multiblock, name, attach)); }

    public static <S extends IMultiblockState> void extendMultiblock(MultiblockRegistration<S> multiblock, Callback<S> callback, String name, BlockPos... valid) { putMultiblock(multiblock.id(), new MultiblockCallbackWrapper<>(callback, multiblock, name, valid)); }

    public static void register(ResourceLocation blockEntityType, CallbackOwner<?> owner) { CALLBACKS.put(blockEntityType, owner); }

    private static void putMultiblock(ResourceLocation id, CallbackOwner<?> owner) {
        CALLBACKS.put(id.withPath(path -> path + MultiblockRegistrationBuilder.MASTER_BE_SUFFIX), owner);
        CALLBACKS.put(id.withPath(path -> path + MultiblockRegistrationBuilder.DUMMY_BE_SUFFIX), owner);
    }

    public static Map<ResourceLocation, CallbackOwner<?>> all() { return Collections.unmodifiableMap(CALLBACKS); }
}
