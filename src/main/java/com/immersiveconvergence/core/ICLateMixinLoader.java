package com.immersiveconvergence.core;

import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class ICLateMixinLoader implements ILateMixinLoader {
    @Override public List<String> getMixinConfigs() { return Arrays.asList("mixins.immersiveconvergence.json", "mixins.immersiveconvergence.ip.json"); }
}
