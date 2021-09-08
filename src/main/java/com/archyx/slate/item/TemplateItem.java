package com.archyx.slate.item;

import com.archyx.slate.Slate;
import com.archyx.slate.action.Action;
import com.archyx.slate.action.click.ClickAction;
import com.archyx.slate.item.provider.TemplateItemProvider;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateItem<C> extends MenuItem {

    private final Map<C, SlotPos> positions;
    private final Map<C, ItemStack> baseItems;
    private final ItemStack defaultBaseItem;
    private final SlotPos defaultPosition;
    private final TemplateItemProvider<C> provider;

    public TemplateItem(Slate slate, String name, Map<C, ItemStack> baseItems, ItemStack defaultBaseItem, String displayName, List<String> lore, Map<ClickAction, List<Action>> actions, Map<C, SlotPos> positions, SlotPos defaultPosition, TemplateItemProvider<C> provider) {
        super(slate, name, displayName, lore, actions);
        this.positions = positions;
        this.baseItems = baseItems;
        this.provider = provider;
        this.defaultBaseItem = defaultBaseItem;
        this.defaultPosition = defaultPosition;
    }

    public SlotPos getPosition(C context) {
        return positions.get(context);
    }

    public Map<C, ItemStack> getBaseItems() {
        Map<C, ItemStack> clonedItems = new HashMap<>();
        for (Map.Entry<C, ItemStack> entry : baseItems.entrySet()) {
            clonedItems.put(entry.getKey(), entry.getValue().clone());
        }
        return clonedItems;
    }

    @Nullable
    public ItemStack getDefaultBaseItem() {
        if (defaultBaseItem != null) {
            return defaultBaseItem.clone();
        }
        return null;
    }

    @Nullable
    public SlotPos getDefaultPosition() {
        return defaultPosition;
    }

    @Nullable
    public TemplateItemProvider<C> getProvider() {
        return provider;
    }

}
