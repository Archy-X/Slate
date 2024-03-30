package com.archyx.slate.info;

import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;

public class TemplatePlaceholderInfo<T> extends PlaceholderInfo {

    private final T value;

    public TemplatePlaceholderInfo(Player player, String placeholder, ActiveMenu menu, PlaceholderData data, T value) {
        super(player, placeholder, menu, data);
        this.value = value;
    }

    public T value() {
        return value;
    }
}
