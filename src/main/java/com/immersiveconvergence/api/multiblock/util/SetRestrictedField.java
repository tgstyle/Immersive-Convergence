package com.immersiveconvergence.api.multiblock.util;

import com.immersiveconvergence.core.lib.ICLib;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Pair;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.data.loading.DatagenModLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class SetRestrictedField<T> implements Supplier<T> {
    private static final InitializationTracker COMMON_FIELDS = new InitializationTracker();
    private final InitializationTracker tracker;
    private T value;

    private SetRestrictedField(InitializationTracker tracker) { this.tracker = tracker; }

    public static <T> SetRestrictedField<T> common() { return COMMON_FIELDS.make(); }

    public static void startInitializing() { COMMON_FIELDS.startInitialization(); }

    public static void lock() { COMMON_FIELDS.lock(); }

    public void setValue(T value) {
        Preconditions.checkState(tracker.state == TrackerState.INITIALIZING);
        String currentMod = ModLoadingContext.get().getActiveNamespace();
        Preconditions.checkState(DatagenModLoader.isRunningDataGen() || ICLib.MODID.equals(currentMod), "Restricted fields may only be set by Immersive Convergence, current mod is %s", currentMod);
        this.value = value;
    }

    @Override public T get() { return Preconditions.checkNotNull(value); }

    public boolean isInitialized() { return value != null; }

    private static class InitializationTracker {
        private final List<Pair<Exception, SetRestrictedField<?>>> fields = new ArrayList<>();
        private TrackerState state = TrackerState.OPEN;

        synchronized <T> SetRestrictedField<T> make() {
            Preconditions.checkState(state != TrackerState.LOCKED);
            SetRestrictedField<T> result = new SetRestrictedField<>(this);
            fields.add(Pair.of(new RuntimeException("Field created here"), result));
            return result;
        }

        synchronized void startInitialization() {
            Preconditions.checkState(state == TrackerState.OPEN);
            state = TrackerState.INITIALIZING;
        }

        synchronized void lock() {
            Preconditions.checkState(state == TrackerState.INITIALIZING);
            for (Pair<Exception, SetRestrictedField<?>> field : fields) {
                if (!field.getSecond().isInitialized()) { throw new RuntimeException(field.getFirst()); }
            }
            state = TrackerState.LOCKED;
        }
    }

    private enum TrackerState { OPEN, INITIALIZING, LOCKED }
}
