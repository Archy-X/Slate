package com.archyx.slate.item.provider;

import com.archyx.slate.menu.ActiveMenu;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public interface TemplateItemProvider<C> {

    /**
     * Gets the context class for this item template
     *
     * @return The class object for this item template
     */
    Class<C> getContext();

    /**
     * Replaces a requested placeholder from display name or lore.
     * Can be used to localize content or inject contextual information.
     *
     * @param placeholder The placeholder name to replace, without identifiers
     * @param player The player that is viewing the menu
     * @param context The context for this item
     * @return The text to replace the placeholder with
     */
    String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data, C context);

    /**
     * Gets a set of defined contexts that are the only valid contexts for this template.
     *
     * @return The set of valid contexts, or null if any context is valid.
     */
    Set<C> getDefinedContexts(Player player, ActiveMenu activeMenu);

    default void onInitialize(Player player, ActiveMenu activeMenu) {}

    default void onInitialize(Player player, ActiveMenu activeMenu, C context) {}

    default void onClick(Player player, InventoryClickEvent event, ItemStack item, SlotPos pos, ActiveMenu activeMenu, C context) {}

    /**
     * Method for modifying the base item through code, such as player specific item meta
     *
     * @param baseItem The base item before modification
     * @return The base item after modification
     */
    default ItemStack onItemModify(ItemStack baseItem, Player player, ActiveMenu activeMenu, C context) {
        return baseItem;
    }

    default SlotPos getSlotPos(Player player, ActiveMenu activeMenu, C context) {
        return null;
    }

    default <T> String resolveComponent(String[] components, Player player, ActiveMenu menu, T context) {
        return null;
    }

}
