package com.immersiveconvergence.api.block;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IUnlistedProperty;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

@SuppressWarnings("unused")
public class ICProperties {
    public static final PropertyDirection FACING_ALL = PropertyDirection.create("facing");
    public static final PropertyDirection FACING_HORIZONTAL = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyBoolInverted MULTIBLOCKSLAVE = PropertyBoolInverted.create("_0multiblockslave");
    public static final PropertyBoolInverted DYNAMICRENDER = PropertyBoolInverted.create("_1dynamicrender");
    public static final PropertySet CONNECTIONS = new PropertySet("conns");

    public static IUnlistedProperty<?>[] appendConnections(IUnlistedProperty<?>[] unlisted) {
        int extra = com.immersiveconvergence.api.ICMods.immersiveEngineering() ? 2 : 1;
        IUnlistedProperty<?>[] array = java.util.Arrays.copyOf(unlisted, unlisted.length + extra);
        array[unlisted.length] = CONNECTIONS;
        if (extra == 2) { array[unlisted.length + 1] = com.immersiveconvergence.common.block.IETileBridge.connectionsProperty(); }
        return array;
    }

    public static final PropertySideConfig[] SIDECONFIG = {
            new PropertySideConfig("sideconfig_down"),
            new PropertySideConfig("sideconfig_up"),
            new PropertySideConfig("sideconfig_north"),
            new PropertySideConfig("sideconfig_south"),
            new PropertySideConfig("sideconfig_west"),
            new PropertySideConfig("sideconfig_east")
    };

    public static final PropertyBoolInverted[] BOOLEANS = {
            PropertyBoolInverted.create("boolean0"),
            PropertyBoolInverted.create("boolean1"),
            PropertyBoolInverted.create("boolean2")
    };

    public static final IUnlistedProperty<HashMap<String, String>> OBJ_TEXTURE_REMAP = new IUnlistedProperty<HashMap<String, String>>() {
        @Override public String getName() { return "obj_texture_remap"; }

        @Override public boolean isValid(HashMap<String, String> value) { return true; }

        @Override @SuppressWarnings("unchecked") public Class<HashMap<String, String>> getType() { return (Class<HashMap<String, String>>)(Class<?>)HashMap.class; }

        @Override public String valueToString(HashMap<String, String> value) { return value.toString(); }
    };

    public static final IUnlistedProperty<TileEntity> TILEENTITY_PASSTHROUGH = new IUnlistedProperty<TileEntity>() {
        @Override public String getName() { return "tileentity_passthrough"; }

        @Override public boolean isValid(TileEntity value) { return true; }

        @Override public Class<TileEntity> getType() { return TileEntity.class; }

        @Override public String valueToString(TileEntity value) { return value.toString(); }
    };

    public static class PropertyBoolInverted extends PropertyHelper<Boolean> {
        private static final ImmutableList<Boolean> ALLOWED_VALUES = ImmutableList.of(false, true);

        protected PropertyBoolInverted(String name) { super(name, Boolean.class); }

        public static PropertyBoolInverted create(String name) { return new PropertyBoolInverted(name); }

        @Override @Nonnull public Collection<Boolean> getAllowedValues() { return ALLOWED_VALUES; }

        @Override @Nonnull public Optional<Boolean> parseValue(@Nonnull String value) { return Optional.of(Boolean.parseBoolean(value)); }

        @Override @Nonnull public String getName(Boolean value) { return value.toString(); }
    }

    public static class PropertySideConfig implements IUnlistedProperty<ICSideConfig> {
        private final String name;

        public PropertySideConfig(String name) { this.name = name; }

        @Override public String getName() { return name; }

        @Override public boolean isValid(ICSideConfig value) { return true; }

        @Override public Class<ICSideConfig> getType() { return ICSideConfig.class; }

        @Override public String valueToString(ICSideConfig value) { return value.toString(); }
    }

    public static class PropertySet implements IUnlistedProperty<Set<?>> {
        private final String name;

        public PropertySet(String name) { this.name = name; }

        @Override public String getName() { return name; }

        @Override public boolean isValid(Set<?> value) { return value != null; }

        @Override @SuppressWarnings("unchecked") public Class<Set<?>> getType() { return (Class<Set<?>>)(Class<?>)Set.class; }

        @Override public String valueToString(Set<?> value) { return value.toString(); }
    }
}
