package com.archyx.slate.menu;

import com.archyx.slate.fill.FillItem;
import com.archyx.slate.item.option.Option;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

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

    default Set<Option<?>> getOptions() {
        return new HashSet<>();
    }

    default FillItem getFillItem() {
        return null;
    }

}
