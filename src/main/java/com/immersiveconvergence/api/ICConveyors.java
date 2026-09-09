package com.immersiveconvergence.api;

import blusunrize.immersiveengineering.api.tool.ConveyorHandler;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;
import java.util.Map;

@SuppressWarnings("unused")
public class ICConveyors {
    private static Map<ResourceLocation, Class<?>> classRegistry;
    private static Map<Class<?>, ResourceLocation> reverseClassRegistry;

    public static void registerBelt(String path, Class<?> beltClass) throws ReflectiveOperationException {
        if (classRegistry == null) { resolveRegistries(); }
        ResourceLocation rl = new ResourceLocation(ICIntegration.MODID, path);
        classRegistry.put(rl, beltClass);
        reverseClassRegistry.put(beltClass, rl);
    }

    @SuppressWarnings("unchecked")
    private static void resolveRegistries() throws ReflectiveOperationException {
        Field classRegistryField = ConveyorHandler.class.getDeclaredField("classRegistry");
        Field reverseClassRegistryField = ConveyorHandler.class.getDeclaredField("reverseClassRegistry");
        classRegistryField.setAccessible(true);
        reverseClassRegistryField.setAccessible(true);
        classRegistry = (Map<ResourceLocation, Class<?>>)classRegistryField.get(null);
        reverseClassRegistry = (Map<Class<?>, ResourceLocation>)reverseClassRegistryField.get(null);
    }
}
