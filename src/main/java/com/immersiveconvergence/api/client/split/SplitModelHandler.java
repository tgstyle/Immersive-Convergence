package com.immersiveconvergence.api.client.split;

import com.immersiveconvergence.ImmersiveConvergence;
import com.immersiveconvergence.api.multiblock.TemplateMultiblock;
import com.immersiveconvergence.common.util.ICLogger;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = ImmersiveConvergence.MODID, value = Side.CLIENT)
public class SplitModelHandler {
    private static final Map<String, List<Machine>> machinesByNamespace = new HashMap<>();

    public static void register(String namespace, String masterFile, Supplier<TemplateMultiblock> instance) { register(namespace, masterFile, null, masterFile + "_slave", null, false, instance); }

    public static void register(String namespace, String masterFile, String masterType, String slaveFile, String slaveType, boolean splitDynamicRender, Supplier<TemplateMultiblock> instance) {
        machinesByNamespace.computeIfAbsent(namespace, key -> new ArrayList<>()).add(new Machine(masterFile, masterType, slaveFile, slaveType, splitDynamicRender, instance));
    }

    @SubscribeEvent public static void onModelBake(ModelBakeEvent event) {
        IRegistry<ModelResourceLocation, IBakedModel> registry = event.getModelRegistry();
        Map<ModelResourceLocation, Machine> matches = new HashMap<>();
        for (ModelResourceLocation mrl : registry.getKeys()) {
            List<Machine> machines = machinesByNamespace.get(mrl.getNamespace());
            if (machines == null) { continue; }
            for (Machine machine : machines) {
                if (machine.covers(mrl)) {
                    matches.put(mrl, machine);
                    break;
                }
            }
        }
        Map<String, IBakedModel> bases = new HashMap<>();
        for (Map.Entry<ModelResourceLocation, Machine> entry : matches.entrySet()) {
            ModelResourceLocation mrl = entry.getKey();
            Machine machine = entry.getValue();
            if (machine.isMaster(mrl) && "false".equals(value(mrl.getVariant(), "_0multiblockslave"))) { bases.put(machine.key(mrl.getVariant()), registry.getObject(mrl)); }
        }
        Map<String, BakedSplitModel> wrappers = new HashMap<>();
        for (Map.Entry<ModelResourceLocation, Machine> entry : matches.entrySet()) {
            ModelResourceLocation mrl = entry.getKey();
            Machine machine = entry.getValue();
            String key = machine.key(mrl.getVariant());
            IBakedModel base = bases.get(key);
            if (base == null) { continue; }
            BakedSplitModel wrapper = wrappers.get(key);
            if (wrapper == null) {
                Set<BlockPos> offsets = machine.instance.get().worldOffsetsFromMaster(facingOf(mrl.getVariant()), mirroredOf(mrl.getVariant()));
                if (offsets.isEmpty()) {
                    ICLogger.error("No template cells for " + mrl.getNamespace() + ":" + machine.masterFile + " - leaving its models unsplit");
                    continue;
                }
                wrapper = new BakedSplitModel(base, offsets);
                wrappers.put(key, wrapper);
            }
            registry.putObject(mrl, wrapper);
        }
    }

    private static String value(String variant, String property) {
        for (String pair : variant.split(",")) {
            int eq = pair.indexOf('=');
            if (eq > 0 && pair.substring(0, eq).equals(property)) { return pair.substring(eq + 1); }
        }
        return null;
    }

    private static EnumFacing facingOf(String variant) {
        String name = value(variant, "facing");
        EnumFacing facing = name == null ? null : EnumFacing.byName(name);
        return facing == null ? EnumFacing.NORTH : facing;
    }

    private static boolean mirroredOf(String variant) { return "true".equals(value(variant, "boolean0")); }

    private static final class Machine {
        final String masterFile, masterType, slaveFile, slaveType;
        final boolean splitDynamicRender;
        final Supplier<TemplateMultiblock> instance;

        Machine(String masterFile, String masterType, String slaveFile, String slaveType, boolean splitDynamicRender, Supplier<TemplateMultiblock> instance) {
            this.masterFile = masterFile;
            this.masterType = masterType;
            this.slaveFile = slaveFile;
            this.slaveType = slaveType;
            this.splitDynamicRender = splitDynamicRender;
            this.instance = instance;
        }

        boolean isMaster(ModelResourceLocation mrl) { return mrl.getPath().equals(masterFile) && (masterType == null || masterType.equals(value(mrl.getVariant(), "type"))); }

        boolean isSlave(ModelResourceLocation mrl) { return mrl.getPath().equals(slaveFile) && (slaveType == null || slaveType.equals(value(mrl.getVariant(), "type"))); }

        boolean covers(ModelResourceLocation mrl) {
            String variant = mrl.getVariant();
            if (variant.contains("inventory") || value(variant, "facing") == null) { return false; }
            if (!splitDynamicRender && "true".equals(value(variant, "_1dynamicrender"))) { return false; }
            return isMaster(mrl) || isSlave(mrl);
        }

        String key(String variant) { return masterFile + "|" + masterType + "|" + facingOf(variant) + "|" + mirroredOf(variant) + "|" + value(variant, "boolean1") + "|" + (splitDynamicRender ? value(variant, "_1dynamicrender") : ""); }
    }
}
