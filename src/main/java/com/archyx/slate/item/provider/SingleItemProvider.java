package com.archyx.slate.item.provider;

import com.archyx.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;

public interface SingleItemProvider {

    /**
     * Replaces a requested placeholder from display name or lore.
     * Can be used to localize content or inject contextual information.
     *
     * @param placeholder The placeholder name to replace, without identifiers
     * @param player The player that is viewing the menu
     * @return The text to replace the placeholder with
     */
    String onPlaceholderReplace(String placeholder, Player player, ActiveMenu menu, PlaceholderType type);

}
