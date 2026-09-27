package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.core.lib.ICLib;

import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class MultiblockDataLoader {
    private static final Map<String, Optional<MultiblockData>> JAR = new ConcurrentHashMap<>();
    private static final List<Runnable> LISTENERS = new CopyOnWriteArrayList<>();
    private static final Gson GSON = new Gson();
    @Nullable private static volatile Pack pack;
    private static final AtomicInteger GENERATION = new AtomicInteger();

    private record Pack(Map<ResourceLocation, String> json, Map<ResourceLocation, MultiblockData> data, Map<ResourceLocation, int[]> sizes) {}

    @Nullable public static MultiblockData loadMultiblockData(Class<?> owner, String modid, String multiblockName) {
        Pack current = pack;
        if (current != null) {
            MultiblockData data = current.data.get(ResourceLocation.fromNamespaceAndPath(modid, multiblockName));
            if (data != null) { return data; }
        }
        return loadJarData(owner, modid, multiblockName);
    }

    @Nullable public static MultiblockData loadJarData(Class<?> owner, String modid, String multiblockName) { return JAR.computeIfAbsent(modid + ":" + multiblockName, key -> Optional.ofNullable(readJar(owner, modid, multiblockName))).orElse(null); }

    public static boolean absent(Class<?> owner, String modid, String multiblockName) {
        Pack current = pack;
        if (current != null && current.data.containsKey(ResourceLocation.fromNamespaceAndPath(modid, multiblockName))) { return false; }
        return owner.getResource(jarPath(modid, multiblockName)) == null;
    }

    @Nullable static int[] packSize(String modid, String multiblockName) {
        Pack current = pack;
        return current == null ? null : current.sizes.get(ResourceLocation.fromNamespaceAndPath(modid, multiblockName));
    }

    public static byte[] encode() {
        Pack current = pack;
        Map<ResourceLocation, String> json = current == null ? Map.of() : current.json;
        Map<ResourceLocation, int[]> sizes = current == null ? Map.of() : current.sizes;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(new GZIPOutputStream(bytes))) {
            out.writeInt(json.size());
            for (Map.Entry<ResourceLocation, String> entry : json.entrySet()) {
                writeId(out, entry.getKey());
                byte[] text = entry.getValue().getBytes(StandardCharsets.UTF_8);
                out.writeInt(text.length);
                out.write(text);
            }
            out.writeInt(sizes.size());
            for (Map.Entry<ResourceLocation, int[]> entry : sizes.entrySet()) {
                writeId(out, entry.getKey());
                for (int value : entry.getValue()) { out.writeInt(value); }
            }
        }
        catch (IOException e) { throw new UncheckedIOException(e); }
        return bytes.toByteArray();
    }

    public static void applyEncoded(byte[] payload) {
        Map<ResourceLocation, String> json = new HashMap<>();
        Map<ResourceLocation, int[]> sizes = new HashMap<>();
        try (DataInputStream in = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(payload)))) {
            for (int i = in.readInt(); i > 0; i--) { json.put(readId(in), new String(in.readNBytes(in.readInt()), StandardCharsets.UTF_8)); }
            for (int i = in.readInt(); i > 0; i--) { sizes.put(readId(in), new int[]{in.readInt(), in.readInt(), in.readInt()}); }
        }
        catch (IOException | RuntimeException e) {
            ICLib.IC_LOGGER.error("Error reading multiblock data sent by the server", e);
            return;
        }
        apply(json, sizes);
    }

    private static void writeId(DataOutputStream out, ResourceLocation id) throws IOException {
        out.writeUTF(id.getNamespace());
        out.writeUTF(id.getPath());
    }

    private static ResourceLocation readId(DataInputStream in) throws IOException { return ResourceLocation.fromNamespaceAndPath(in.readUTF(), in.readUTF()); }

    public static int generation() { return GENERATION.get(); }

    public static void onReload(Runnable listener) { LISTENERS.add(listener); }

    private static boolean unchanged(@Nullable Pack current, Map<ResourceLocation, String> json, Map<ResourceLocation, int[]> sizes) {
        if (current == null || !current.json().equals(json) || current.sizes().size() != sizes.size()) { return false; }
        for (Map.Entry<ResourceLocation, int[]> entry : sizes.entrySet()) {
            if (!Arrays.equals(current.sizes().get(entry.getKey()), entry.getValue())) { return false; }
        }
        return true;
    }

    static void apply(Map<ResourceLocation, String> json, Map<ResourceLocation, int[]> sizes) {
        if (unchanged(pack, json, sizes)) { return; }
        Map<ResourceLocation, MultiblockData> data = new HashMap<>();
        for (Map.Entry<ResourceLocation, String> entry : json.entrySet()) {
            try (Reader reader = new StringReader(entry.getValue())) {
                MultiblockData parsed = parse(reader);
                if (parsed != null) { data.put(entry.getKey(), parsed); }
            }
            catch (Exception e) { ICLib.IC_LOGGER.error("Error loading multiblock data {}, using the built-in copy", entry.getKey(), e); }
        }
        pack = new Pack(Map.copyOf(json), Map.copyOf(data), Map.copyOf(sizes));
        GENERATION.incrementAndGet();
        ICLib.IC_LOGGER.info("Loaded multiblock data for {} machines", data.size());
        for (Runnable listener : LISTENERS) {
            try { listener.run(); }
            catch (RuntimeException e) { ICLib.IC_LOGGER.error("Error applying reloaded multiblock data", e); }
        }
    }

    private static String jarPath(String modid, String multiblockName) { return "/data/" + modid + "/multiblocks/" + multiblockName + ".json"; }

    @Nullable private static MultiblockData readJar(Class<?> owner, String modid, String multiblockName) {
        String path = jarPath(modid, multiblockName);
        try (InputStream is = owner.getResourceAsStream(path)) {
            if (is == null) {
                ICLib.IC_LOGGER.error("{} JSON resource not found at {}", multiblockName, path);
                return null;
            }
            try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) { return parse(reader); }
        }
        catch (Exception e) {
            ICLib.IC_LOGGER.error("Error loading {} from JSON", multiblockName, e);
            return null;
        }
    }

    @Nullable private static MultiblockData parse(Reader reader) {
        MultiblockData data = GSON.fromJson(reader, MultiblockData.class);
        if (data == null || data.pointsOfInterest == null) { return data; }
        for (PoIJSONSchema poi : data.pointsOfInterest) {
            if (poi.facing == null) { continue; }
            if (poi.facing.isJsonPrimitive()) { poi.relativeFaces.add(face(poi.facing.getAsString())); }
            else if (poi.facing.isJsonArray()) {
                for (JsonElement el : poi.facing.getAsJsonArray()) { poi.relativeFaces.add(face(el.getAsString())); }
            }
        }
        return data;
    }

    @Nullable private static RelativeBlockFace face(String str) { return str.isEmpty() || str.equalsIgnoreCase("any") ? null : RelativeBlockFace.valueOf(str.toUpperCase(Locale.ROOT)); }
}
