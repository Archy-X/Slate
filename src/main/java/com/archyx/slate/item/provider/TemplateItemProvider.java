package com.archyx.slate.item.provider;

import com.archyx.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;

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
    String onPlaceholderReplace(String placeholder, Player player, ActiveMenu menu, C context);

    /**
     * Gets a set of defined contexts that are the only valid contexts for this template.
     *
     * @return The set of valid contexts, or null if any context is valid.
     */
    Set<C> getDefinedContexts();

}
