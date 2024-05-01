package dev.aurelium.slate.function;

import org.bukkit.entity.Player;

import java.util.Locale;

@FunctionalInterface
public interface LocaleProvider {

    /**
     * Gets the locale of a player. This is used in context objects like {@link dev.aurelium.slate.info.MenuInfo}
     * to easily get the locale of a player.
     *
     * @param player the player
     * @return the locale
     */
    Locale get(Player player);

}
