package com.archyx.slate.item.builder;

import com.archyx.slate.Slate;
import com.archyx.slate.context.ContextGroup;
import com.archyx.slate.item.MenuItem;
import com.archyx.slate.item.TemplateItem;
import com.archyx.slate.lore.LoreLine;
import com.archyx.slate.position.PositionProvider;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateItemBuilder<C> extends MenuItemBuilder {

    private Class<C> contextClass;
    private Map<C, PositionProvider> positions;
    private Map<C, ItemStack> baseItems;
    private Map<C, String> contextualDisplayNames;
    private Map<C, List<LoreLine>> contextualLore;
    private ItemStack defaultBaseItem;
    private SlotPos defaultPosition;
    private Map<String, ContextGroup> contextGroups = new HashMap<>();

    public TemplateItemBuilder(Slate slate) {
        super(slate);
    }

    public TemplateItemBuilder<C> contextClass(Class<C> contextClass) {
        this.contextClass = contextClass;
        return this;
    }

    public TemplateItemBuilder<C> positions(Map<C, PositionProvider> positions) {
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

    public TemplateItemBuilder<C> contextualDisplayNames(Map<C, String> contextualDisplayNames) {
        this.contextualDisplayNames = contextualDisplayNames;
        return this;
    }

    public TemplateItemBuilder<C> contextualLore(Map<C, List<LoreLine>> contextualLore) {
        this.contextualLore = contextualLore;
        return this;
    }

    public TemplateItemBuilder<C> contextGroups(Map<String, ContextGroup> contextGroups) {
        this.contextGroups = contextGroups;
        return this;
    }

    @Override
    public MenuItem build() {
        return new TemplateItem<>(slate, name, contextClass, baseItems, defaultBaseItem, displayName, lore, contextualDisplayNames, contextualLore, actions, positions, defaultPosition, options, contextGroups);
    }
}
