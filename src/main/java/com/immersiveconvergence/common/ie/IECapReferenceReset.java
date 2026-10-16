package com.immersiveconvergence.common.ie;

import com.immersiveconvergence.core.lib.ICLib;

import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import net.minecraftforge.common.util.LazyOptional;
import javax.annotation.Nullable;
import java.lang.reflect.Field;

public final class IECapReferenceReset {
    private static final String TYPE_NAME = "blusunrize.immersiveengineering.api.utils.CapabilityReference$BECapReference";
    @Nullable private static final Fields FIELDS = resolve();

    private IECapReferenceReset() {}

    public static void reset(CapabilityReference<?> ref) {
        if (FIELDS == null || !FIELDS.type().isInstance(ref)) { return; }
        try {
            FIELDS.currentCap().set(ref, LazyOptional.empty());
            FIELDS.lastPos().set(ref, null);
            FIELDS.lastWorld().set(ref, null);
            FIELDS.lastBE().set(ref, null);
        }
        catch (IllegalAccessException e) { throw new IllegalStateException(e); }
    }

    @Nullable private static Fields resolve() {
        try {
            Class<?> type = Class.forName(TYPE_NAME, false, CapabilityReference.class.getClassLoader());
            return new Fields(type, open(type, "currentCap"), open(type, "lastPos"), open(type, "lastWorld"), open(type, "lastBE"));
        }
        catch (ReflectiveOperationException | RuntimeException e) {
            ICLib.IC_LOGGER.error("Could not reach the fields of {}; IE fluid pipes will keep stale neighbor capabilities until reloaded", TYPE_NAME, e);
            return null;
        }
    }

    private static Field open(Class<?> type, String name) throws NoSuchFieldException {
        Field field = type.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    private record Fields(Class<?> type, Field currentCap, Field lastPos, Field lastWorld, Field lastBE) {}
}
