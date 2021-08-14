package com.archyx.slate.item.active;

import com.archyx.slate.item.TemplateItem;

public class ActiveTemplateItem<C> extends ActiveItem {

    private final TemplateItem<C> item;

    public ActiveTemplateItem(TemplateItem<C> item) {
        this.item = item;
    }

    public TemplateItem<C> getItem() {
        return item;
    }

}
