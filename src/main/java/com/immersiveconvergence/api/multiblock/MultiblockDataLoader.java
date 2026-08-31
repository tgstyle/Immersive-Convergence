package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.core.lib.ICLib;

import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MultiblockDataLoader {
    private static final Map<String, MultiblockData> CACHE = new HashMap<>();

    public static MultiblockData loadMultiblockData(Class<?> owner, String modid, String multiblockName) {
        String key = modid + ":" + multiblockName;
        if (CACHE.containsKey(key)) { return CACHE.get(key); }
        MultiblockData data = null;
        try {
            InputStream is = owner.getResourceAsStream("/assets/" + modid + "/multiblocks/" + multiblockName + ".json");
            if (is != null) {
                JsonReader reader = new JsonReader(new InputStreamReader(is));
                Gson gson = new Gson();
                data = gson.fromJson(reader, MultiblockData.class);
                reader.close();
                for (PoIJSONSchema poi : data.pointsOfInterest) {
                    if (poi.facing != null) {
                        if (poi.facing.isJsonPrimitive()) {
                            String str = poi.facing.getAsString();
                            RelativeBlockFace face = str.isEmpty() || str.equalsIgnoreCase("any") ? null : RelativeBlockFace.valueOf(str.toUpperCase(Locale.ROOT));
                            poi.relativeFaces.add(face);
                        }
                        else if (poi.facing.isJsonArray()) {
                            for (JsonElement el : poi.facing.getAsJsonArray()) {
                                String str = el.getAsString();
                                RelativeBlockFace face = str.isEmpty() || str.equalsIgnoreCase("any") ? null : RelativeBlockFace.valueOf(str.toUpperCase(Locale.ROOT));
                                poi.relativeFaces.add(face);
                            }
                        }
                    }
                }
            }
            else { ICLib.IC_LOGGER.error("{} JSON resource not found at /assets/{}/multiblocks/{}.json", multiblockName, modid, multiblockName); }
        }
        catch (Exception e) { ICLib.IC_LOGGER.error("Error loading {} from JSON", multiblockName, e); }
        if (data != null) { CACHE.put(key, data); }
        return data;
    }
}
