package com.archyx.slate.item;

import com.archyx.slate.Slate;
import com.archyx.slate.action.Action;
import com.archyx.slate.action.click.ClickAction;
import com.archyx.slate.lore.LoreLine;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateItem<C> extends MenuItem {

    private final Map<C, SlotPos> positions;
    private final Map<C, ItemStack> baseItems;
    private final Map<C, String> contextualDisplayNames;
    private final Map<C, List<LoreLine>> contextualLore;
    private final ItemStack defaultBaseItem;
    private final SlotPos defaultPosition;

    public TemplateItem(Slate slate, String name, Map<C, ItemStack> baseItems, ItemStack defaultBaseItem, String displayName, List<LoreLine> lore, Map<C, String> contextualDisplayNames, Map<C, List<LoreLine>> contextualLore, Map<ClickAction, List<Action>> actions, Map<C, SlotPos> positions, SlotPos defaultPosition, Map<String, Object> options) {
        super(slate, name, displayName, lore, actions, options);
        this.positions = positions;
        this.baseItems = baseItems;
        this.contextualDisplayNames = contextualDisplayNames;
        this.contextualLore = contextualLore;
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
    public String getContextualDisplayName(C context) {
        return contextualDisplayNames.get(context);
    }

    /**
     * Gets the active display name for the given context. If the context has a contextual display name, it will be returned.
     * Otherwise, the default display name will be returned.
     *
     * @param context The context
     * @return The active display name
     */
    @Nullable
    public String getActiveDisplayName(C context) {
        String contextualDisplayName = getContextualDisplayName(context);
        if (contextualDisplayName != null) {
            return contextualDisplayName;
        }
        return getDisplayName();
    }

    @Nullable
    public List<LoreLine> getContextualLore(C context) {
        return contextualLore.get(context);
    }

    /**
     * Gets the active lore for the given context. If the context has contextual lore, it will be returned.
     * Otherwise, the default lore will be returned.
     *
     * @param context The context
     * @return The active lore
     */
    @Nullable
    public List<LoreLine> getActiveLore(C context) {
        List<LoreLine> contextualLore = getContextualLore(context);
        if (contextualLore != null) {
            return contextualLore;
        }
        return getLore();
    }

}
