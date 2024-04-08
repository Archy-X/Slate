package dev.aurelium.slate.info;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.item.provider.PlaceholderData;
import dev.aurelium.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;

import java.util.Locale;

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

    public Player player() {
        return player;
    }

    public String placeholder() {
        return placeholder;
    }

    public ActiveMenu menu() {
        return menu;
    }

    public PlaceholderData data() {
        return data;
    }

    public Locale locale() {
        return slate.getGlobalOptions().localeProvider().get(player);
    }
}
