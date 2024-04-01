package com.archyx.slate.info;

import com.archyx.slate.Slate;
import com.archyx.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;

import java.util.Locale;

public class MenuInfo {

    private final Slate slate;
    private final Player player;
    private final ActiveMenu menu;

    public MenuInfo(Slate slate, Player player, ActiveMenu menu) {
        this.slate = slate;
        this.player = player;
        this.menu = menu;
    }

    public Player player() {
        return player;
    }

    public ActiveMenu menu() {
        return menu;
    }

    public Locale locale() {
        return slate.getGlobalOptions().localeProvider().get(player);
    }
}
