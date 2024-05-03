package dev.aurelium.slate.item;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.Action;
import dev.aurelium.slate.action.trigger.ClickTrigger;
import dev.aurelium.slate.context.ContextGroup;
import dev.aurelium.slate.inv.content.SlotPos;
import dev.aurelium.slate.lore.LoreLine;
import dev.aurelium.slate.position.PositionProvider;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateItem<C> extends MenuItem {

    private final Class<C> contextClass;
    private final Map<C, PositionProvider> positions;
    private final Map<C, ItemStack> baseItems;
    private final Map<C, String> contextualDisplayNames;
    private final Map<C, List<LoreLine>> contextualLore;
    private final ItemStack defaultBaseItem;
    private final SlotPos defaultPosition;
    private final Map<String, ContextGroup> contextGroups;

    public TemplateItem(Slate slate, String name, Class<C> contextClass, Map<C, ItemStack> baseItems, ItemStack defaultBaseItem, String displayName, List<LoreLine> lore, Map<C, String> contextualDisplayNames, Map<C, List<LoreLine>> contextualLore, Map<ClickTrigger, List<Action>> actions, Map<C, PositionProvider> positions, SlotPos defaultPosition, Map<String, Object> options, Map<String, ContextGroup> contextGroups) {
        super(slate, name, displayName, lore, actions, options);
        this.contextClass = contextClass;
        this.positions = positions;
        this.baseItems = baseItems;
        this.contextualDisplayNames = contextualDisplayNames;
        this.contextualLore = contextualLore;
        this.defaultBaseItem = defaultBaseItem;
        this.defaultPosition = defaultPosition;
        this.contextGroups = contextGroups;
    }

    public Class<C> getContextClass() {
        return contextClass;
    }

    public PositionProvider getPosition(C context) {
        return positions.get(context);
    }

    public Map<C, PositionProvider> getPositionsMap() {
        return positions;
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

    public Map<String, ContextGroup> getContextGroups() {
        return contextGroups;
    }
}
