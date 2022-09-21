package com.archyx.slate.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DefaultMenuProvider implements MenuProvider {

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu menu) {
        return placeholder;
    }

    @Override
    public int getPages(Player player, ActiveMenu activeMenu) {
        return 1;
    }

    @Override
    public ItemStack getFillItem(Player player, ActiveMenu activeMenu) {
        return null;
    }
}
