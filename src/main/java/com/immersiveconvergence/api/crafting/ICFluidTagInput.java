package com.immersiveconvergence.api.crafting;

import com.immersiveconvergence.api.util.ICUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ICFluidTagInput implements Predicate<FluidStack> {
    protected final Either<TagKey<Fluid>, List<ResourceLocation>> fluidTag;
    protected final int amount;
    @Nullable protected final CompoundTag nbtTag;

    public ICFluidTagInput(Either<TagKey<Fluid>, List<ResourceLocation>> matching, int amount, @Nullable CompoundTag nbtTag) {
        this.fluidTag = matching;
        this.amount = amount;
        this.nbtTag = nbtTag;
    }

    public ICFluidTagInput(TagKey<Fluid> tag, int amount, @Nullable CompoundTag nbtTag) { this(Either.left(tag), amount, nbtTag); }

    public ICFluidTagInput(ResourceLocation tag, int amount, @Nullable CompoundTag nbtTag) { this(TagKey.create(Registries.FLUID, tag), amount, nbtTag); }

    public ICFluidTagInput(TagKey<Fluid> tag, int amount) { this(tag, amount, null); }

    public static ICFluidTagInput deserialize(JsonElement input) {
        if (!(input instanceof JsonObject json)) { throw new IllegalArgumentException("FluidTagWithSize can only be deserialized from a JsonObject"); }
        ResourceLocation tag = ResourceLocation.parse(GsonHelper.getAsString(json, "tag"));
        int amount = GsonHelper.getAsInt(json, "amount");
        if (!GsonHelper.isValidNode(json, "nbt")) { return new ICFluidTagInput(tag, amount, null); }
        try { return new ICFluidTagInput(tag, amount, ICUtils.parseNbtFromJson(json.get("nbt"))); }
        catch (CommandSyntaxException e) { throw new JsonParseException(e); }
    }

    @Nonnull public JsonElement serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("tag", fluidTag.orThrow().location().toString());
        json.addProperty("amount", amount);
        if (nbtTag != null) { json.addProperty("nbt", nbtTag.toString()); }
        return json;
    }

    public void write(FriendlyByteBuf out) {
        List<ResourceLocation> matching = fluidTag.map(tag -> tagFluids(tag).map(fluid -> Objects.requireNonNull(ForgeRegistries.FLUIDS.getKey(fluid))).toList(), list -> list);
        out.writeVarInt(matching.size());
        for (ResourceLocation rl : matching) { out.writeResourceLocation(rl); }
        out.writeInt(amount);
        out.writeBoolean(nbtTag != null);
        if (nbtTag != null) { out.writeNbt(nbtTag); }
    }

    @Override public boolean test(@Nullable FluidStack stack) {
        if (stack == null) { return false; }
        return testIgnoringAmount(stack) && stack.getAmount() >= amount;
    }

    public boolean testIgnoringAmount(@Nullable FluidStack stack) {
        if (stack == null) { return false; }
        if (!fluidTag.map(tag -> stack.getFluid().defaultFluidState().is(tag), list -> list.contains(ForgeRegistries.FLUIDS.getKey(stack.getFluid())))) { return false; }
        if (nbtTag != null) { return stack.hasTag() && nbtTag.equals(stack.getTag()); }
        return true;
    }

    @Nonnull public List<FluidStack> getMatchingFluidStacks() { return fluidTag.map(ICFluidTagInput::tagFluids, list -> list.stream().map(ForgeRegistries.FLUIDS::getValue)).map(fluid -> new FluidStack(fluid, amount, nbtTag)).toList(); }

    private static Stream<Fluid> tagFluids(TagKey<Fluid> tag) { return Objects.requireNonNull(ForgeRegistries.FLUIDS.tags()).getTag(tag).stream(); }

    public int getAmount() { return amount; }

    public static ICFluidTagInput read(FriendlyByteBuf input) {
        int count = input.readVarInt();
        List<ResourceLocation> matching = new ArrayList<>(count);
        for (int i = 0; i < count; ++i) { matching.add(input.readResourceLocation()); }
        int amount = input.readInt();
        CompoundTag nbt = input.readBoolean() ? input.readNbt() : null;
        return new ICFluidTagInput(Either.right(matching), amount, nbt);
    }
}
