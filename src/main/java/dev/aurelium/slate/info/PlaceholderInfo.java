package dev.aurelium.slate.info;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.builder.GlobalBehavior;
import dev.aurelium.slate.item.provider.PlaceholderData;
import dev.aurelium.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;

import java.util.Locale;

/**
 * Represents contextual information about a placeholder in a menu.
 */
public class PlaceholderInfo {

    private final Slate slate;
    private final Player player;
    private final String placeholder;
    private final ActiveMenu menu;
    private final PlaceholderData data;

    public PlaceholderInfo(Slate slate, Player player, String placeholder, ActiveMenu menu, PlaceholderData data) {
        this.slate = slate;
        this.player = player;
        this.placeholder = placeholder;
        this.menu = menu;
        this.data = data;
    }

    /**
     * Gets the player viewing the menu.
     *
     * @return the player
     */
    public Player player() {
        return player;
    }

    /**
     * Gets the name of the placeholder string being replaced. This is the placeholder string in the menu file without
     * the curly braces.
     *
     * @return the placeholder
     */
    public String placeholder() {
        return placeholder;
    }

    /**
     * Gets the {@link ActiveMenu} instance of the menu. This can be used to get properties of the menu and
     * modify the menu while its open.
     *
     * @return the active menu
     */
    public ActiveMenu menu() {
        return menu;
    }

    /**
     * Gets data about the specific placeholder, such as where it is located (display name or lore),
     * the active style, and list data if applicable.
     *
     * @return the placeholder data
     */
    public PlaceholderData data() {
        return data;
    }

    /**
     * Gets the locale of the player viewing the menu as defined by the {@link GlobalBehavior#localeProvider()}.
     * If the locale provider is not set, this will always return {@code Locale.ENGLISH}. This is useful if you
     * have player-dependent locales.
     *
     * @return the locale of the player
     */
    public Locale locale() {
        return slate.getGlobalBehavior().localeProvider().get(player);
    }
}
