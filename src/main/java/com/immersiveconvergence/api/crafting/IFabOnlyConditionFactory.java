package com.immersiveconvergence.api.crafting;

import com.immersiveconvergence.api.ICMods;

import com.google.gson.JsonObject;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;
import java.util.function.BooleanSupplier;

@SuppressWarnings("unused") public final class IFabOnlyConditionFactory implements IConditionFactory {
    @Override public BooleanSupplier parse(JsonContext context, JsonObject json) { return () -> ICMods.ieContent() && !ICMods.immersiveEngineering(); }
}
