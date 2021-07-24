package com.archyx.slate.item.provider;

import org.bukkit.entity.Player;

public interface SingleItemProvider {

    String onPlaceholderReplace(String placeholder, Player player);

}
