package com.immersiveconvergence.api.crafting;

import com.immersiveconvergence.api.ICMods;

import com.google.gson.JsonObject;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;
import java.util.function.BooleanSupplier;

public final class IEEnabledConditionFactory implements IConditionFactory {
    @Override public BooleanSupplier parse(JsonContext context, JsonObject json) { return ICMods::immersiveEngineering; }
}
