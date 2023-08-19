package com.archyx.slate.component;

import com.archyx.slate.lore.LoreLine;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MenuComponent {

    private final Class<?> contextClass;
    private final List<LoreLine> lore;

    public MenuComponent(@Nullable Class<?> contextClass, List<LoreLine> lore) {
        this.contextClass = contextClass;
        this.lore = lore;
    }

    @Nullable
    public Class<?> getContextClass() {
        return contextClass;
    }

    public List<LoreLine> getLore() {
        return lore;
    }
}
