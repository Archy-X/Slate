package com.archyx.slate.item.active;

import com.archyx.slate.item.SingleItem;
import fr.minuskube.inv.ItemClickData;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ActiveSingleItem extends ActiveItem {

    private final SingleItem item;
    private Consumer<ItemClickData> clickListener;

    public ActiveSingleItem(SingleItem item) {
        this.item = item;
    }

    public SingleItem getItem() {
        return item;
    }

    @Nullable
    public Consumer<ItemClickData> getClickListener() {
        return clickListener;
    }

    /**
     * Adds a listener to be executed when the item is clicked.
     * The specified consumer can use ItemClickData to get information from the click, such as the event or player.
     * {@link ItemClickData#getEvent()} can be checked and then cast to InventoryClickEvent.
     *
     * @param clickListener The click listener Consumer
     */
    public void setClickListener(Consumer<ItemClickData> clickListener) {
        this.clickListener = clickListener;
    }

}
