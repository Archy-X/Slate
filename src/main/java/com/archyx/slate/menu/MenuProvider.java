package com.archyx.slate.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public interface MenuProvider {

    /**
     * Calls when a menu is in the process of being opened.
     * This will call before any items are actually put into the menu.
     *
     * @param player The player that is viewing the menu
     * @param menu The active menu instance
     */
    default void onOpen(Player player, ActiveMenu menu) {}

    /**
     * Replaces a requested placeholder from the menu title.
     * Can be used to localize content or inject contextual information.
     *
     * @param placeholder The placeholder name to replace, without identifiers
     * @param player The player that is viewing the menu
     * @return The text to replace the placeholder with
     */
    String onPlaceholderReplace(String placeholder, Player player, ActiveMenu menu);

    /**
     * Gets the number of pages the menu should have, defaults to 1
     *
     * @param player The player that is viewing the menu
     * @return The number of pages
     */
    default int getPages(Player player, ActiveMenu activeMenu) {
        return 1;
    }

    default ItemStack getFillItem(Player player, ActiveMenu activeMenu) {
        return null;
    }

    default void onUpdate(Player player, ActiveMenu menu) {}

    default Map<String, Object> getDefaultProperties(ActiveMenu activeMenu) {
        return new HashMap<>();
    }

}
