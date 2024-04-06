package com.archyx.slate.component;

import com.archyx.slate.lore.LoreLine;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record MenuComponent(Class<?> contextClass, List<LoreLine> lore) {

    @Override
    @Nullable
    public Class<?> contextClass() {
        return contextClass;
    }
}
