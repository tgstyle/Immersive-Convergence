package com.immersiveconvergence.api.compat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ICComputerTables {
    private static final List<ICComputerTable<?>> TABLES = new ArrayList<>();

    private ICComputerTables() {}

    @SuppressWarnings("unused") public static void register(ICComputerTable<?> table) { TABLES.add(table); }

    public static List<ICComputerTable<?>> all() { return Collections.unmodifiableList(TABLES); }
}
