package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.network.MessageMultiblockData;
import com.immersiveconvergence.core.lib.ICLib;
import com.immersiveconvergence.core.network.PacketHandler;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import javax.annotation.Nonnull;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@EventBusSubscriber(modid = ICLib.MODID)
public final class MultiblockDataReloader extends SimplePreparableReloadListener<MultiblockDataReloader.Prepared> {
    private static final String FOLDER = "multiblocks";
    private static final String EXTENSION = ".json";
    private static final Gson GSON = new Gson();

    public record Prepared(Map<ResourceLocation, String> json, Map<ResourceLocation, int[]> sizes) {}

    @SubscribeEvent public static void addListener(AddReloadListenerEvent event) { event.addListener(new MultiblockDataReloader()); }

    @SubscribeEvent public static void sync(OnDatapackSyncEvent event) {
        List<ServerPlayer> remote = event.getRelevantPlayers().filter(player -> !player.server.isSingleplayerOwner(player.getGameProfile())).toList();
        if (remote.isEmpty()) { return; }
        MessageMultiblockData message = new MessageMultiblockData(MultiblockDataLoader.encode());
        for (ServerPlayer player : remote) { PacketHandler.sendToPlayer(player, message); }
    }

    @Override @Nonnull protected Prepared prepare(@Nonnull ResourceManager manager, @Nonnull ProfilerFiller profiler) {
        Map<ResourceLocation, String> json = new HashMap<>();
        Map<ResourceLocation, int[]> sizes = new HashMap<>();
        for (Map.Entry<ResourceLocation, Resource> entry : manager.listResources(FOLDER, file -> file.getPath().endsWith(EXTENSION)).entrySet()) {
            ResourceLocation file = entry.getKey();
            String path = file.getPath();
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(file.getNamespace(), path.substring(FOLDER.length() + 1, path.length() - EXTENSION.length()));
            try (Reader reader = entry.getValue().openAsReader()) { json.put(id, GSON.toJson(JsonParser.parseReader(reader))); }
            catch (Exception e) {
                ICLib.IC_LOGGER.error("Error reading multiblock data {}", file, e);
                continue;
            }
            Optional<Resource> structure = manager.getResource(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), GenericShape.STRUCTURE_FOLDER + id.getPath() + ".nbt"));
            if (structure.isEmpty()) { continue; }
            try (InputStream is = structure.get().open()) {
                int[] size = GenericShape.readSize(is);
                if (size != null) { sizes.put(id, size); }
            }
            catch (Exception e) { ICLib.IC_LOGGER.error("Error reading the structure size of {}", id, e); }
        }
        return new Prepared(json, sizes);
    }

    @Override protected void apply(@Nonnull Prepared prepared, @Nonnull ResourceManager manager, @Nonnull ProfilerFiller profiler) { MultiblockDataLoader.apply(prepared.json(), prepared.sizes()); }
}
