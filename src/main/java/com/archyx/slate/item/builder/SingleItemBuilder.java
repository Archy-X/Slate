package com.archyx.slate.item.builder;

import com.archyx.slate.Slate;
import com.archyx.slate.item.MenuItem;
import com.archyx.slate.item.SingleItem;
import com.archyx.slate.item.provider.SingleItemProvider;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.inventory.ItemStack;

public class SingleItemBuilder extends MenuItemBuilder {

    private SlotPos position;
    private ItemStack baseItem;
    private SingleItemProvider provider;

    public SingleItemBuilder(Slate slate) {
        super(slate);
    }

    public SingleItemBuilder position(SlotPos position) {
        this.position = position;
        return this;
    }

    public SingleItemBuilder baseItem(ItemStack baseItem) {
        this.baseItem = baseItem;
        return this;
    }

    public SingleItemBuilder provider(SingleItemProvider provider) {
        this.provider = provider;
        return this;
    }

    @Override
    public MenuItem build() {
        return new SingleItem(slate, name, baseItem, displayName, lore, position, provider);
    }
}
