package com.archyx.slate.item.active;

import com.archyx.slate.item.SingleItem;

public class ActiveSingleItem extends ActiveItem {

    private final SingleItem item;

    public ActiveSingleItem(SingleItem item) {
        this.item = item;
    }

    public SingleItem getItem() {
        return item;
    }

}
