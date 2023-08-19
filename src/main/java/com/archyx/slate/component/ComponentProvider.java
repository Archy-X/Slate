package com.archyx.slate.component;

import org.bukkit.entity.Player;

public interface ComponentProvider {

    <T> String onPlaceholderReplace(String placeholder, Player player, T context);

    <T> boolean shouldShow(Player player, T context);

}
