package com.immersiveconvergence.api.multiblock.blocks.env;

import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;

import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.Nullable;
import java.util.function.Supplier;

@NonExtendable public interface IInitialMultiblockContext<State extends IMultiblockState> extends ICommonMultiblockContext {
    Supplier<@Nullable Level> levelSupplier();

    Runnable getMarkDirtyRunnable();

    Runnable getSyncRunnable();
}
