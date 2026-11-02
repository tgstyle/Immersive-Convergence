package com.immersiveconvergence.core.compat.opencomputers;

import li.cil.oc.api.machine.LimitReachedException;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class ICOCBlockRef {
    private final Level level;
    private final BlockPos pos;
    private BlockEntity current;

    public ICOCBlockRef(BlockEntity blockEntity) {
        this.level = blockEntity.getLevel();
        this.pos = blockEntity.getBlockPos();
        this.current = blockEntity;
    }

    public BlockEntity current() { return current; }

    public void requireServerThread() throws LimitReachedException {
        MinecraftServer server = level.getServer();
        if (server != null && !server.isSameThread()) { throw new LimitReachedException(); }
    }

    public void refresh() {
        if (!current.isRemoved() || !level.isLoaded(pos)) { return; }
        BlockEntity next = level.getBlockEntity(pos);
        if (next == null || next.getType() != current.getType()) { return; }
        current = next;
    }

    public BlockEntity resolve() {
        refresh();
        if (current.isRemoved()) { throw new IllegalStateException("Machine is not loaded"); }
        return current;
    }
}
