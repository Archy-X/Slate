package com.archyx.slate.item.provider;

import com.archyx.slate.menu.ActiveMenu;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public interface SingleItemProvider {

    /**
     * Replaces a requested placeholder from display name or lore.
     * Can be used to localize content or inject contextual information.
     *
     * @param placeholder The placeholder name to replace, without identifiers
     * @param player The player that is viewing the menu
     * @return The text to replace the placeholder with
     */
    String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data);

    default void onInitialize(Player player, ActiveMenu activeMenu) {}

    default void onClick(Player player, InventoryClickEvent event, ItemStack item, SlotPos pos, ActiveMenu activeMenu) {}

    /**
     * Method for modifying the base item through code, such as player specific item meta
     *
     * @param baseItem The base item before modification
     * @return The base item after modification
     */
    default ItemStack onItemModify(ItemStack baseItem, Player player, ActiveMenu activeMenu) {
        return baseItem;
    }

    default String resolveComponent(String[] components, Player player, ActiveMenu menu) {
        return null;
    }

}
