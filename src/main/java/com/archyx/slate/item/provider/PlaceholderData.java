package com.archyx.slate.item.provider;

import com.archyx.slate.lore.ListData;
import org.jetbrains.annotations.Nullable;

public class PlaceholderData {

    private final PlaceholderType type;
    private final String style;
    private final ListData listData;

    public PlaceholderData(PlaceholderType type, String style, @Nullable ListData listData) {
        this.type = type;
        this.style = style;
        this.listData = listData;
    }

    public PlaceholderType getType() {
        return type;
    }

    public String getStyle() {
        return style;
    }

    public boolean isList() {
        return listData != null && listData.getInterval() > 0;
    }

    public ListData getListData() {
        if (isList()) {
            return listData;
        } else {
            return new ListData("", 0);
        }
    }
}
