package com.immersiveconvergence.core;

import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class ICLateMixinLoader implements ILateMixinLoader {
    @Override public List<String> getMixinConfigs() { return Collections.singletonList("mixins.immersiveconvergence.json"); }
}
