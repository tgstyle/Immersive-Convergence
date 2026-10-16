package com.immersiveconvergence.api.crafting.conditions;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.core.lib.ICLib;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import javax.annotation.Nonnull;

public final class ICIEEnabledCondition implements ICondition {
    public static final ICIEEnabledCondition INSTANCE = new ICIEEnabledCondition();
    private static final ResourceLocation NAME = ResourceLocation.fromNamespaceAndPath(ICLib.MODID, "ie_enabled");

    private ICIEEnabledCondition() {}

    @Override public ResourceLocation getID() { return NAME; }

    @Override public boolean test(@Nonnull IContext context) { return ICMods.immersiveEngineering(); }

    @Override public String toString() { return "ie_enabled"; }

    public static final class Serializer implements IConditionSerializer<ICIEEnabledCondition> {
        public static final Serializer INSTANCE = new Serializer();

        private Serializer() {}

        @Override public void write(@Nonnull JsonObject json, @Nonnull ICIEEnabledCondition value) {}

        @Override public ICIEEnabledCondition read(@Nonnull JsonObject json) { return ICIEEnabledCondition.INSTANCE; }

        @Override public ResourceLocation getID() { return NAME; }
    }
}
