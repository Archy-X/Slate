package com.archyx.slate.item.provider;

public class PlaceholderData {

    private final PlaceholderType type;
    private final String style;

    public PlaceholderData(PlaceholderType type, String style) {
        this.type = type;
        this.style = style;
    }

    public PlaceholderType getType() {
        return type;
    }

    public String getStyle() {
        return style;
    }
}
