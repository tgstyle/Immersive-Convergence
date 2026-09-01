package com.immersiveconvergence.api.multiblock;

@SuppressWarnings("unused")
public interface IDisassemblingAware {
    boolean ic$isDisassembling();

    default boolean ic$isAssembled() { return !ic$isDisassembling(); }
}
