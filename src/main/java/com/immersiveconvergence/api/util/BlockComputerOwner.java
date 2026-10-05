package com.immersiveconvergence.api.util;

import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackOwner;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.UnaryOperator;

public class BlockComputerOwner<T extends BlockEntity> extends CallbackOwner<T> {
    private final UnaryOperator<T> master;

    public BlockComputerOwner(Class<T> type, String name, UnaryOperator<T> master, Callback<? super T> callback) {
        super(type, name);
        this.master = master;
        addAdditional(callback);
    }

    public BlockComputerOwner(Class<T> type, String name, Callback<? super T> callback) { this(type, name, UnaryOperator.identity(), callback); }

    @Override public boolean canAttachTo(T candidate) { return master.apply(candidate) != null; }

    @Override public T preprocess(T candidate) { return master.apply(candidate); }
}
