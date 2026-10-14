package com.immersiveconvergence.api.multiblock.util;

import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "RedundantSuppression"}) public abstract class CapabilityReference<T> {
    protected final Capability<T> cap;

    protected CapabilityReference(Capability<T> cap) { this.cap = Objects.requireNonNull(cap); }

    public static <T> CapabilityReference<T> forBlockEntityAt(BlockEntity local, Supplier<DirectionalBlockPos> pos, Capability<T> cap) { return new BECapReference<>(local::getLevel, pos, cap); }

    @Nullable public abstract T getNullable();

    @Nonnull public T get() { return Objects.requireNonNull(getNullable()); }

    public abstract boolean isPresent();

    private static class BECapReference<T> extends CapabilityReference<T> {
        private final Supplier<Level> getLevel;
        private final Supplier<DirectionalBlockPos> getPos;
        @Nonnull private LazyOptional<T> currentCap = LazyOptional.empty();
        private DirectionalBlockPos lastPos;
        private Level lastWorld;
        private BlockEntity lastBE;

        BECapReference(Supplier<Level> getLevel, Supplier<DirectionalBlockPos> getPos, Capability<T> cap) {
            super(cap);
            this.getLevel = getLevel;
            this.getPos = getPos;
        }

        @Nullable @Override public T getNullable() {
            updateLazyOptional();
            return currentCap.resolve().orElse(null);
        }

        @Override public boolean isPresent() {
            updateLazyOptional();
            return currentCap.isPresent();
        }

        private void updateLazyOptional() {
            Level currWorld = getLevel.get();
            DirectionalBlockPos currPos = getPos.get();
            if (currWorld == null || currPos == null) {
                currentCap = LazyOptional.empty();
                lastWorld = null;
                lastPos = null;
                lastBE = null;
            }
            else if (currWorld != lastWorld || !currPos.equals(lastPos) || !currentCap.isPresent() || (lastBE != null && lastBE.isRemoved())) {
                if (currentCap.isPresent() && lastBE != null && lastBE.isRemoved()) { ICLib.IC_LOGGER.warn("The tile entity {} (class {}) was removed, but the value {} provided by it for the capability {} is still marked as valid. This is likely a bug in the mod(s) adding the tile entity/the capability", lastBE, lastBE.getClass(), currentCap.orElseThrow(RuntimeException::new), cap.getName()); }
                lastBE = SafeChunkUtils.getSafeBE(currWorld, currPos.position());
                currentCap = lastBE != null ? lastBE.getCapability(cap, currPos.side()) : LazyOptional.empty();
                lastWorld = currWorld;
                lastPos = currPos;
            }
        }
    }
}
