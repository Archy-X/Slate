package com.archyx.slate.component;

import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;

public interface ComponentProvider {

    <T> String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data, ComponentData componentData, T context);

    <T> boolean shouldShow(Player player, ActiveMenu activeMenu, T context);

    default <T> int getInstances(Player player, ActiveMenu activeMenu, T context) {
        return 1;
    }

}
