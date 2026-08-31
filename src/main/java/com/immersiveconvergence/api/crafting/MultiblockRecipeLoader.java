package com.immersiveconvergence.api.crafting;

import com.immersiveconvergence.common.util.ICLogger;
import com.immersiveconvergence.common.util.ICResources;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.commons.io.FilenameUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class MultiblockRecipeLoader {
    public interface TypeHandler { void register(JsonObject json, JsonContext context); }

    private static final JsonParser PARSER = new JsonParser();
    private static final Map<String, TypeHandler> HANDLERS = new HashMap<>();
    private static final Set<String> CONDITION_MODIDS = new HashSet<>();

    public static void registerType(String type, TypeHandler handler) { HANDLERS.put(type, handler); }

    public static void loadRecipes(String modid, String folder) {
        registerConditions(modid);
        JsonContext context = new JsonContext(modid);
        Map<String, JsonObject> files = new TreeMap<>();
        ModContainer mod = Loader.instance().getIndexedModList().get(modid);
        CraftingHelper.findFiles(mod, "assets/" + modid + "/" + folder, root -> true, (root, file) -> {
            readFile(files, root, file);
            return true;
        }, true, true);
        Path overrides = ICResources.overrideRoot(modid).resolve(folder);
        try {
            Files.createDirectories(overrides);
            try (Stream<Path> stream = Files.walk(overrides)) { stream.filter(Files::isRegularFile).forEach(file -> readFile(files, overrides, file)); }
        }
        catch (IOException e) { ICLogger.error("Failed to read recipe overrides - " + e.getMessage()); }
        int loaded = 0;
        for (Map.Entry<String, JsonObject> entry : files.entrySet()) {
            if (parse(modid, entry.getKey(), entry.getValue(), context)) { loaded++; }
        }
        ICLogger.info("Loaded " + loaded + " multiblock recipes from " + files.size() + " files for " + modid);
    }

    private static void registerConditions(String modid) {
        if (!CONDITION_MODIDS.add(modid)) { return; }
        CraftingHelper.register(new ResourceLocation(modid, "fluid_exists"), (IConditionFactory) (context, json) -> {
            String fluid = JsonUtils.getString(json, "fluid");
            return () -> FluidRegistry.isFluidRegistered(fluid);
        });
    }

    private static void readFile(Map<String, JsonObject> files, Path root, Path file) {
        String relative = root.relativize(file).toString();
        if (!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.startsWith("_")) { return; }
        String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/");
        try (BufferedReader reader = Files.newBufferedReader(file)) { files.put(name, PARSER.parse(reader).getAsJsonObject()); }
        catch (JsonParseException | IllegalStateException | IOException e) { ICLogger.error("Failed to read recipe " + name + " - " + e.getMessage()); }
    }

    private static boolean parse(String modid, String name, JsonObject json, JsonContext context) {
        try {
            if (json.entrySet().isEmpty()) { return false; }
            if (json.has("conditions") && !CraftingHelper.processConditions(JsonUtils.getJsonArray(json, "conditions"), context)) { return false; }
            if (JsonUtils.getString(json, "type").equals(modid + ":conditional")) {
                for (JsonElement element : JsonUtils.getJsonArray(json, "recipes")) {
                    JsonObject entry = element.getAsJsonObject();
                    if (!entry.has("conditions") || CraftingHelper.processConditions(JsonUtils.getJsonArray(entry, "conditions"), context)) { return register(name, JsonUtils.getJsonObject(entry, "recipe"), context); }
                }
                return false;
            }
            return register(name, json, context);
        }
        catch (JsonParseException | IllegalStateException | IllegalArgumentException e) {
            ICLogger.error("Failed to load recipe " + name + " - " + e.getMessage());
            return false;
        }
    }

    private static boolean register(String name, JsonObject json, JsonContext context) {
        String type = JsonUtils.getString(json, "type");
        TypeHandler handler = HANDLERS.get(type);
        if (handler == null) {
            ICLogger.error("Unknown recipe type " + type + " in " + name);
            return false;
        }
        handler.register(json, context);
        return true;
    }

    public static FluidStack getFluidStack(JsonObject json, String key) {
        JsonObject object = JsonUtils.getJsonObject(json, key);
        String name = JsonUtils.getString(object, "fluid");
        if (!FluidRegistry.isFluidRegistered(name)) { throw new JsonSyntaxException("Unknown fluid " + name); }
        return new FluidStack(FluidRegistry.getFluid(name), JsonUtils.getInt(object, "amount"));
    }

    public static FluidStack optionalFluidStack(JsonObject json, String key) {
        if (!json.has(key)) { return null; }
        return getFluidStack(json, key);
    }
}
