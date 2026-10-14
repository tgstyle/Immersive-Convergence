package com.immersiveconvergence.api.crafting;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.utils.ItemUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused") public class ICFluidTagInput extends FluidTagInput {
    public ICFluidTagInput(Either<TagKey<Fluid>, List<ResourceLocation>> matching, int amount, @Nullable CompoundTag nbtTag) { super(matching, amount, nbtTag); }

    public ICFluidTagInput(ResourceLocation tag, int amount, @Nullable CompoundTag nbtTag) { super(tag, amount, nbtTag); }

    public ICFluidTagInput(TagKey<Fluid> tag, int amount) { super(tag, amount); }

    public static ICFluidTagInput deserialize(JsonElement input) {
        if (!(input instanceof JsonObject json)) { throw new IllegalArgumentException("FluidTagWithSize can only be deserialized from a JsonObject"); }
        ResourceLocation tag = ResourceLocation.parse(GsonHelper.getAsString(json, "tag"));
        int amount = GsonHelper.getAsInt(json, "amount");
        if (!GsonHelper.isValidNode(json, "nbt")) { return new ICFluidTagInput(tag, amount, null); }
        try { return new ICFluidTagInput(tag, amount, ItemUtils.parseNbtFromJson(json.get("nbt"))); }
        catch (CommandSyntaxException e) { throw new JsonParseException(e); }
    }

    public static ICFluidTagInput read(FriendlyByteBuf input) {
        int count = input.readVarInt();
        List<ResourceLocation> matching = new ArrayList<>(count);
        for (int i = 0; i < count; ++i) { matching.add(input.readResourceLocation()); }
        int amount = input.readInt();
        CompoundTag nbt = input.readBoolean() ? input.readNbt() : null;
        return new ICFluidTagInput(Either.right(matching), amount, nbt);
    }

    @Override public ICFluidTagInput withAmount(int amount) { return new ICFluidTagInput(fluidTag, amount, nbtTag); }

    static ICFluidTagInput copyOf(FluidTagInput input) {
        if (input instanceof ICFluidTagInput ic) { return ic; }
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            input.write(buffer);
            return read(buffer);
        }
        finally { buffer.release(); }
    }
}
