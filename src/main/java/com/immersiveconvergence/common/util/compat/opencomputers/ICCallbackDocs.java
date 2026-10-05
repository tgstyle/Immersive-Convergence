package com.immersiveconvergence.common.util.compat.opencomputers;

final class ICCallbackDocs {
    static final String ENERGY_STORED = "function():int -- returns the amount of energy stored";
    static final String MAX_ENERGY_STORED = "function():int -- returns the maximum amount of energy that can be stored";
    static final String ENABLE_COMPUTER_CONTROL = "function(enabled:bool):nil -- Enables or disables computer control for the attached machine";
    static final String SET_ENABLED = "function(enabled:bool):nil -- Enables or disables the machine. Call \"enableComputerControl(true)\" before using this and disable computer control before removing the computer";
    static final String IS_ACTIVE = "function():boolean -- checks whether the machine is currently active";
    static final String TANK_INFO = "function():table -- returns information about the tank";
    static final String PROCESS = "function():int -- returns the progress of the current process";
    static final String MAX_PROCESS = "function():int -- returns the length of the current process";
    static final String BURN_TIME = "function():int -- returns the remaining burn time of the current fuel";
    static final String INPUT_STACK = "function():table -- returns the stack in the input slot";
    static final String FUEL_STACK = "function():table -- returns the stack in the fuel slot";
    static final String OUTPUT_STACK = "function():table -- returns the stack in the output slot";

    private ICCallbackDocs() {}
}
