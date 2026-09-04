package com.immersiveconvergence.api.client.split;

import com.immersiveconvergence.ImmersiveConvergence;
import com.immersiveconvergence.common.util.ICLogger;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = ImmersiveConvergence.MODID, value = Side.CLIENT)
public class MultiblockTextureHandler {
    public static final String FOLDER = "multiblock";
    private static final String EXTENSION = ".png";
    private static final Set<String> NAMESPACES = new LinkedHashSet<>();

    public static void register(String namespace) { NAMESPACES.add(namespace); }

    @SubscribeEvent public static void onTextureStitch(TextureStitchEvent.Pre event) {
        TextureMap map = event.getMap();
        for (String namespace : NAMESPACES) {
            ModContainer mod = Loader.instance().getIndexedModList().get(namespace);
            if (mod == null) {
                ICLogger.warn("No mod container for " + namespace + " - its " + FOLDER + " textures are not registered");
                continue;
            }
            int[] count = {0};
            CraftingHelper.findFiles(mod, "assets/" + namespace + "/textures/" + FOLDER, root -> true, (root, file) -> {
                String relative = root.relativize(file).toString().replace('\\', '/');
                if (!Files.isRegularFile(file) || !relative.endsWith(EXTENSION)) { return true; }
                map.registerSprite(new ResourceLocation(namespace, FOLDER + "/" + relative.substring(0, relative.length() - EXTENSION.length())));
                count[0]++;
                return true;
            }, true, true);
            ICLogger.info("Registered " + count[0] + " " + FOLDER + " sprites for " + namespace);
        }
    }
}
