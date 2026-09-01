package com.immersiveconvergence.api.client.split;

import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = ICLib.MODID, value = Dist.CLIENT)
public class SplitModelHandler {
    private static final Map<ResourceLocation, Machine> MACHINES = new HashMap<>();

    public static void register(Block block, ResourceLocation multiblock) { register(block, multiblock, Direction.NORTH); }

    public static void register(Block block, ResourceLocation multiblock, Direction unrotated) {
        put(block, unrotated, mirrored -> () -> SplitData.fromMultiblock(multiblock, mirrored));
    }

    public static void register(Block block, List<Vec3i> parts, Direction unrotated) {
        SplitData data = SplitData.fromParts(parts);
        put(block, unrotated, mirrored -> () -> data);
    }

    private static void put(Block block, Direction unrotated, java.util.function.Function<Boolean, Supplier<SplitData>> data) {
        ResourceLocation name = BuiltInRegistries.BLOCK.getKey(block);
        MACHINES.put(name, new Machine(unrotated, data));
    }

    @SubscribeEvent public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        Map<ModelResourceLocation, BakedModel> models = event.getModels();
        Map<ModelResourceLocation, Target> targets = new HashMap<>();
        for (ModelResourceLocation mrl : models.keySet()) {
            Machine machine = MACHINES.get(ResourceLocation.fromNamespaceAndPath(mrl.id().getNamespace(), mrl.id().getPath()));
            Direction facing = facingOf(mrl.variant());
            if (machine != null && facing != null && !ModelResourceLocation.INVENTORY_VARIANT.equals(mrl.variant())) { targets.put(mrl, new Target(machine, facing)); }
        }
        Map<ModelResourceLocation, BakedModel> bases = new HashMap<>();
        for (Map.Entry<ModelResourceLocation, Target> e : targets.entrySet()) {
            ModelResourceLocation baseLoc = withFacing(e.getKey(), e.getValue().machine().unrotated());
            BakedModel base = models.get(baseLoc);
            if (base != null) { bases.put(e.getKey(), base); }
            else { ICLib.IC_LOGGER.error("No unrotated variant \"{}\" for split model {}", baseLoc.variant(), e.getKey()); }
        }
        for (Map.Entry<ModelResourceLocation, Target> e : targets.entrySet()) {
            BakedModel base = bases.get(e.getKey());
            if (base == null) { continue; }
            Machine machine = e.getValue().machine();
            boolean mirrored = booleanOf(e.getKey().variant());
            int angle = Math.floorMod((int)e.getValue().facing().toYRot() - (int)machine.unrotated().toYRot(), 360);
            models.put(e.getKey(), new BakedSplitModel<>(base, machine.data().apply(mirrored), BlockModelRotation.by(0, angle), false));
        }
    }

    private static ModelResourceLocation withFacing(ModelResourceLocation mrl, Direction facing) {
        List<String> parts = new ArrayList<>();
        for (String pair : mrl.variant().split(",")) { parts.add(pair.startsWith("facing=") ? "facing=" + facing.getSerializedName() : pair); }
        return new ModelResourceLocation(mrl.id(), String.join(",", parts));
    }

    private static Direction facingOf(String variant) {
        String value = valueOf(variant, "facing");
        return value == null ? null : Direction.byName(value);
    }

    private static boolean booleanOf(String variant) { return "true".equals(valueOf(variant, "mirrored")); }

    private static String valueOf(String variant, String key) {
        for (String pair : variant.split(",")) {
            int eq = pair.indexOf('=');
            if (eq > 0 && pair.substring(0, eq).equals(key)) { return pair.substring(eq + 1); }
        }
        return null;
    }

    private record Target(Machine machine, Direction facing) {}

    private record Machine(Direction unrotated, java.util.function.Function<Boolean, Supplier<SplitData>> data) {}
}
