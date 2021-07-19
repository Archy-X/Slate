package com.archyx.slate.menu;

import org.bukkit.entity.Player;

public interface MenuProvider {

    String replacePlaceholder(String placeholder, Player player);

    int getPages(Player player);

    void onOpen(Player player, MenuInventory menu);

}
