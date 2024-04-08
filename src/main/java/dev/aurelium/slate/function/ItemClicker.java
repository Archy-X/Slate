package dev.aurelium.slate.function;

import dev.aurelium.slate.item.ItemClick;

@FunctionalInterface
public interface ItemClicker {

    void click(ItemClick info);

}
