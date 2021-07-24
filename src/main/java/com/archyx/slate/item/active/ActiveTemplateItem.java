package com.archyx.slate.item.active;

import com.archyx.slate.item.TemplateItem;
import fr.minuskube.inv.ItemClickData;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ActiveTemplateItem<C> extends ActiveItem {

    private final TemplateItem<C> item;
    private BiConsumer<ItemClickData, C> clickListener;

    public ActiveTemplateItem(TemplateItem<C> item) {
        this.item = item;
    }

    public TemplateItem<C> getItem() {
        return item;
    }

    @Nullable
    public BiConsumer<ItemClickData, C> getClickListener() {
        return clickListener;
    }

    /**
     * Adds a listener to be executed when the item is clicked.
     * The specified consumer can use ItemClickData to get information from the click, such as the event or player.
     * {@link ItemClickData#getEvent()} can be checked and then cast to InventoryClickEvent.
     * The listener can also use the context C to define differing behavior
     *
     * @param clickListener The click listener BiConsumer
     */
    public void setClickListener(BiConsumer<ItemClickData, C> clickListener) {
        this.clickListener = clickListener;
    }

    @Nullable
    public Consumer<ItemClickData> bindContext(C context) {
        return clickListener != null ? a -> clickListener.accept(a, context) : null;
    }

}
