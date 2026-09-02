package com.immersiveconvergence.api.integration.jade;

import com.immersiveconvergence.api.integration.DisplayContexts;
import com.immersiveconvergence.api.multiblock.IDisplayContext;
import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum JadeStatusProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    public static final String KEY_STATUS = "gui.immersiveconvergence.status";
    public static final String KEY_ACTIVE = "gui.immersiveconvergence.status_active";
    public static final String KEY_INACTIVE = "gui.immersiveconvergence.status_inactive";
    private static final String TAG = "ICActive";
    private static final long ACTIVE_COOLDOWN_TICKS = 40;
    private static final Map<BlockPos, Long> LAST_ACTIVE_TICK = new ConcurrentHashMap<>();

    @Override public ResourceLocation getUid() { return ICLib.rl("status"); }

    @Override public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        IDisplayContext context = DisplayContexts.of(accessor.getBlockEntity());
        if (context == null) { return; }
        boolean active = context.isActive();
        long now = accessor.getLevel().getGameTime();
        BlockPos pos = accessor.getPosition();
        Long last = LAST_ACTIVE_TICK.get(pos);
        if (active) { LAST_ACTIVE_TICK.put(pos, now); }
        else if (last != null && now - last < ACTIVE_COOLDOWN_TICKS) { active = true; }
        data.putBoolean(TAG, active);
    }

    @Override public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (!data.contains(TAG)) { return; }
        boolean active = data.getBoolean(TAG);
        tooltip.add(Component.translatable(KEY_STATUS, Component.translatable(active ? KEY_ACTIVE : KEY_INACTIVE)).withStyle(active ? ChatFormatting.GREEN : ChatFormatting.RED));
    }
}
