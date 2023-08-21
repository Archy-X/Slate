package com.archyx.slate.item.provider;

import org.jetbrains.annotations.Nullable;

public class PlaceholderData {

    private final PlaceholderType type;
    private final String style;
    private final ListInsertion listInsertion;

    public PlaceholderData(PlaceholderType type, String style, @Nullable ListInsertion listInsertion) {
        this.type = type;
        this.style = style;
        this.listInsertion = listInsertion;
    }

    public PlaceholderType getType() {
        return type;
    }

    public String getStyle() {
        return style;
    }

    public boolean isList() {
        return listInsertion != null;
    }

    public ListInsertion getListInsertion() {
        if (isList()) {
            return listInsertion;
        } else {
            return ListInsertion.NOTHING;
        }
    }
}
