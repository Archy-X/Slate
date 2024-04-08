package dev.aurelium.slate.item.builder;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.item.MenuItem;
import dev.aurelium.slate.item.SingleItem;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.inventory.ItemStack;

public class SingleItemBuilder extends MenuItemBuilder {

    private SlotPos position;
    private ItemStack baseItem;

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

    @Override
    public MenuItem build() {
        return new SingleItem(slate, name, baseItem, displayName, lore, actions, position, options);
    }
}
