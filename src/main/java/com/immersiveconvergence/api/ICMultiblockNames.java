package com.immersiveconvergence.api;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public final class ICMultiblockNames {
    public static final String PREFIX_IE = "desc.immersiveengineering.info.multiblock.";

    private static final Map<String, String> OWNERS = new ConcurrentHashMap<>();

    private ICMultiblockNames() {}

    public static void register(String uniqueNamePrefix, String modid) {
        if (uniqueNamePrefix == null || uniqueNamePrefix.isEmpty() || modid == null || modid.isEmpty()) { return; }
        OWNERS.put(uniqueNamePrefix, modid);
    }

    @SideOnly(Side.CLIENT)
    public static String key(String uniqueName) {
        if (uniqueName == null) { return null; }
        int split = uniqueName.indexOf(':');
        if (split <= 0) { return PREFIX_IE + uniqueName; }
        String modid = OWNERS.get(uniqueName.substring(0, split));
        if (modid == null) { return PREFIX_IE + uniqueName; }
        String owned = "desc." + modid + ".info.multiblock." + uniqueName;
        return I18n.hasKey(owned) ? owned : PREFIX_IE + uniqueName;
    }

    @SideOnly(Side.CLIENT)
    public static String resolve(String key) {
        if (key == null || !key.startsWith(PREFIX_IE)) { return key; }
        return key(key.substring(PREFIX_IE.length()));
    }
}
