package com.immersiveconvergence.api.multiblock.disassembly;

public interface IDisassemblingAware {
    boolean ic$isDisassembling();

    default boolean ic$isAssembled() { return !ic$isDisassembling(); }
}
