package com.immersiveconvergence.api.util;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.multiblock.util.CapabilityReference;
import com.immersiveconvergence.api.multiblock.util.DirectionalBlockPos;
import com.immersiveconvergence.api.multiblock.util.SafeChunkUtils;
import com.immersiveconvergence.common.ie.IERecipeBridge;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.JsonUtils;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Random;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class ICUtils {
    public static final Random RANDOM = new Random();
    public static final RandomSource RANDOM_SOURCE = RandomSource.createNewThreadLocalInstance();
    private static final TagKey<Item> HAMMERS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ICMods.IMMERSIVE_ENGINEERING, "tools/hammers"));
    private static final Gson GSON = new Gson();

    public static FluidStack jsonDeserializeFluidStack(JsonObject jsonObject) {
        FluidStack fluidStack = new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(ResourceLocation.parse(GsonHelper.getAsString(jsonObject, "fluid")))),GsonHelper.getAsInt(jsonObject, "amount"));
        if (GsonHelper.isValidNode(jsonObject, "tag")) { fluidStack.setTag(JsonUtils.readNBT(jsonObject, "tag")); }
        return fluidStack;
    }

    public static JsonElement jsonSerializeFluidStack(FluidStack fluidStack) {
        if (fluidStack == null) { return JsonNull.INSTANCE; }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("fluid", Objects.requireNonNull(ForgeRegistries.FLUIDS.getKey(fluidStack.getFluid())).toString());
        jsonObject.addProperty("amount", fluidStack.getAmount());
        if (fluidStack.hasTag()) { jsonObject.addProperty("tag", String.valueOf(fluidStack.getTag())); }
        return jsonObject;
    }

    public static CompoundTag parseNbtFromJson(JsonElement jsonElement) throws CommandSyntaxException { return TagParser.parseTag(jsonElement.isJsonObject() ? GSON.toJson(jsonElement) : jsonElement.getAsString()); }

    public static ItemStack preferredStack(ItemStack[] stacks) {
        if (ICMods.immersiveEngineering()) { return IERecipeBridge.preferredStack(stacks); }
        return Arrays.stream(stacks).min(Comparator.comparing(stack -> Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(stack.getItem())))).orElseThrow(() -> new IllegalArgumentException("Empty array?"));
    }

    public static boolean isHammer(ItemStack stack) { return stack.is(HAMMERS); }

    public static boolean isChunkSafe(LevelAccessor w, BlockPos pos) { return !SafeChunkUtils.isChunkUnsafe(w, pos); }

    public static <T> CapabilityReference<T> forNeighbor(BlockEntity local, Capability<T> cap, @Nonnull Direction side) { return CapabilityReference.forBlockEntityAt(local, () -> new DirectionalBlockPos(local.getBlockPos().relative(side), side.getOpposite()), cap); }
}
