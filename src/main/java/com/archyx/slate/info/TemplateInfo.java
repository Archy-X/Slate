package com.archyx.slate.info;

import com.archyx.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TemplateInfo<T> extends ItemInfo {

    private final T value;

    public TemplateInfo(Player player, ActiveMenu menu, ItemStack item, T value) {
        super(player, menu, item);
        this.value = value;
    }

    public T value() {
        return value;
    }
}
