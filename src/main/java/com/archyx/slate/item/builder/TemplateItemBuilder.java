package com.archyx.slate.item.builder;

import com.archyx.slate.Slate;
import com.archyx.slate.item.MenuItem;
import com.archyx.slate.item.TemplateItem;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class TemplateItemBuilder<C> extends MenuItemBuilder {

    private Map<C, SlotPos> positions;
    private Map<C, ItemStack> baseItems;
    private ItemStack defaultBaseItem;
    private SlotPos defaultPosition;

    public TemplateItemBuilder(Slate slate) {
        super(slate);
    }

    public TemplateItemBuilder<C> positions(Map<C, SlotPos> positions) {
        this.positions = positions;
        return this;
    }

    public TemplateItemBuilder<C> baseItems(Map<C, ItemStack> baseItems) {
        this.baseItems = baseItems;
        return this;
    }

    public TemplateItemBuilder<C> defaultBaseItem(ItemStack defaultBaseItem) {
        this.defaultBaseItem = defaultBaseItem;
        return this;
    }

    public TemplateItemBuilder<C> defaultPosition(SlotPos defaultPosition) {
        this.defaultPosition = defaultPosition;
        return this;
    }

    @Override
    public MenuItem build() {
        return new TemplateItem<>(slate, name, baseItems, defaultBaseItem, displayName, lore, actions, positions, defaultPosition, options);
    }
}
