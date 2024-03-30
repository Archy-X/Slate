package com.archyx.slate.info;

import com.archyx.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;

public class MenuInfo {

    private final Player player;
    private final ActiveMenu menu;

    public MenuInfo(Player player, ActiveMenu menu) {
        this.player = player;
        this.menu = menu;
    }

    public Player player() {
        return player;
    }

    public ActiveMenu menu() {
        return menu;
    }
}
