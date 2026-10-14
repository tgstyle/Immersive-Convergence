package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.multiblock.MultiblockHandler.IMultiblock;
import com.immersiveconvergence.api.network.MessageMultiblockTemplates;
import com.immersiveconvergence.core.lib.ICLib;
import com.immersiveconvergence.core.network.PacketHandler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.LinkedHashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = ICLib.MODID)
public final class TemplateSyncManager {
    private TemplateSyncManager() {}

    @SubscribeEvent public static void onLogin(PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !(player.level() instanceof ServerLevel level)) { return; }
        Map<ResourceLocation, CompoundTag> templates = new LinkedHashMap<>();
        for (IMultiblock multiblock : MultiblockHandler.getMultiblocks()) {
            if (multiblock instanceof ICTemplateMultiblock template) { templates.put(template.getTemplateLocation(), template.getTemplate(level).template().save(new CompoundTag())); }
        }
        if (!templates.isEmpty()) { PacketHandler.sendToPlayer(player, new MessageMultiblockTemplates(templates)); }
    }
}
