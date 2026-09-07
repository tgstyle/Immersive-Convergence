package com.immersiveconvergence.api.shapes;

public interface IBooleanOp {
    IBooleanOp OR = (p_82705_, p_82706_) -> p_82705_ || p_82706_;

    boolean apply(boolean pPrimaryBool, boolean pSecondaryBool);
}
