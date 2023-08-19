package com.archyx.slate.lore;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class LoreStyles {

    private final Map<Integer, String> styleMap;

    public LoreStyles(Map<Integer, String> styleMap) {
        this.styleMap = styleMap;
    }

    @NotNull
    public String getStyle(int index) {
        return styleMap.getOrDefault(index, "");
    }

    public Map<Integer, String> getStyleMap() {
        return styleMap;
    }
}
