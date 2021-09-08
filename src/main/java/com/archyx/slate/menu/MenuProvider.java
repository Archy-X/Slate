package com.archyx.slate.menu;

import com.archyx.slate.item.provider.SingleItemProvider;
import com.archyx.slate.item.provider.TemplateItemProvider;
import org.bukkit.entity.Player;

public interface MenuProvider {

    default SingleItemProvider getSingleItemProvider(String name) {
        return null;
    }

    default <C> TemplateItemProvider<C> getTemplateItemProvider(String name) {
        return null;
    }

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

}
