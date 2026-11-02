package com.immersiveconvergence.core.integration.opencomputers.ie;

import blusunrize.immersiveengineering.common.util.compat.computers.generic.LuaTypeConverter;
import javax.annotation.Nullable;

public final class IEOCConverter extends LuaTypeConverter {
    @Nullable @Override protected <F> Converter<F, ?> getInternalConverter(Class<F> type) { return null; }
}
