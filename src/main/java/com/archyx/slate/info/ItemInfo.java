package com.archyx.slate.info;

import com.archyx.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemInfo extends MenuInfo {

    private final ItemStack item;

    public ItemInfo(Player player, ActiveMenu menu, ItemStack item) {
        super(player, menu);
        this.item = item;
    }

    public ItemStack item() {
        return item;
    }

}
