package com.immersiveconvergence.api.client.split;

import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = ICLib.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SplitModelHandler {
    private static final String INVENTORY_VARIANT = "inventory";
    private static final Map<ResourceLocation, Machine> MACHINES = new HashMap<>();

    public static void register(Block block, ResourceLocation multiblock) { register(block, multiblock, Direction.NORTH); }

    public static void register(Block block, ResourceLocation multiblock, Direction unrotated) {
        put(block, unrotated, mirrored -> () -> SplitData.fromMultiblock(multiblock, mirrored));
    }

    public static void register(Block block, List<Vec3i> parts, Direction unrotated) {
        SplitData data = SplitData.fromParts(parts);
        put(block, unrotated, mirrored -> () -> data);
    }

    private static void put(Block block, Direction unrotated, Function<Boolean, Supplier<SplitData>> data) {
        MACHINES.put(ForgeRegistries.BLOCKS.getKey(block), new Machine(unrotated, data));
    }

    @SubscribeEvent public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        Map<ResourceLocation, BakedModel> models = event.getModels();
        Map<ModelResourceLocation, Target> targets = new HashMap<>();
        for (ResourceLocation key : models.keySet()) {
            if (!(key instanceof ModelResourceLocation mrl)) { continue; }
            Machine machine = MACHINES.get(ResourceLocation.fromNamespaceAndPath(mrl.getNamespace(), mrl.getPath()));
            Direction facing = facingOf(mrl.getVariant());
            if (machine != null && facing != null && !INVENTORY_VARIANT.equals(mrl.getVariant())) { targets.put(mrl, new Target(machine, facing)); }
        }
        Map<ModelResourceLocation, BakedModel> bases = new HashMap<>();
        for (Map.Entry<ModelResourceLocation, Target> e : targets.entrySet()) {
            ModelResourceLocation baseLoc = withFacing(e.getKey(), e.getValue().machine().unrotated());
            BakedModel base = models.get(baseLoc);
            if (base != null) { bases.put(e.getKey(), base); }
            else { ICLib.IC_LOGGER.error("No unrotated variant \"{}\" for split model {}", baseLoc.getVariant(), e.getKey()); }
        }
        for (Map.Entry<ModelResourceLocation, Target> e : targets.entrySet()) {
            BakedModel base = bases.get(e.getKey());
            if (base == null) { continue; }
            Machine machine = e.getValue().machine();
            boolean mirrored = booleanOf(e.getKey().getVariant());
            int angle = Math.floorMod((int)e.getValue().facing().toYRot() - (int)machine.unrotated().toYRot(), 360);
            models.put(e.getKey(), new BakedSplitModel<>(base, machine.data().apply(mirrored), BlockModelRotation.by(0, angle), false));
        }
    }

    private static ModelResourceLocation withFacing(ModelResourceLocation mrl, Direction facing) {
        List<String> parts = new ArrayList<>();
        for (String pair : mrl.getVariant().split(",")) { parts.add(pair.startsWith("facing=") ? "facing=" + facing.getSerializedName() : pair); }
        return new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(mrl.getNamespace(), mrl.getPath()), String.join(",", parts));
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

    private record Machine(Direction unrotated, Function<Boolean, Supplier<SplitData>> data) {}
}
