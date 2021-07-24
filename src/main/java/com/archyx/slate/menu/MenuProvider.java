package com.archyx.slate.menu;

import org.bukkit.entity.Player;

public interface MenuProvider {

    void onOpen(Player player, ActiveMenu menu);

    String onPlaceholderReplace(String placeholder, Player player);

    default int getPages(Player player) {
        return 1;
    }

}
