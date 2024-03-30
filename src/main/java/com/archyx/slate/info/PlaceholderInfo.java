package com.archyx.slate.info;

import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;

public class PlaceholderInfo {

    private final Player player;
    private final String placeholder;
    private final ActiveMenu menu;
    private final PlaceholderData data;

    public PlaceholderInfo(Player player, String placeholder, ActiveMenu menu, PlaceholderData data) {
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
}
