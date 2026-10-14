package com.immersiveconvergence.api.util;

import com.immersiveconvergence.api.multiblock.blocks.component.RedstoneControl.RSState;
import com.immersiveconvergence.api.multiblock.util.ComputerControlState;

import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;

@SuppressWarnings("unused") public class ICComputerControlCallbacks extends Callback<RSState> {
    public static final ICComputerControlCallbacks INSTANCE = new ICComputerControlCallbacks();

    @ComputerCallable public boolean getEnabled(CallbackEnvironment<RSState> env) { return env.object().getComputerControlState().isEnabled(); }

    @ComputerCallable public void setEnabled(CallbackEnvironment<RSState> env, boolean enable) { env.object().getComputerControlState().setEnabled(enable); }

    @ComputerCallable public void enableComputerControl(CallbackEnvironment<RSState> env, boolean enable) {
        ComputerControlState control = env.object().getComputerControlState();
        if (!enable) {
            control.clear();
            return;
        }
        if (!control.isAttached()) { control.addReference(); }
        control.setEnabled(true);
    }
}
