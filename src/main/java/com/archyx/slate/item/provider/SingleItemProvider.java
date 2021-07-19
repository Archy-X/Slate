package com.archyx.slate.item.provider;

import org.bukkit.entity.Player;

public interface SingleItemProvider {

    String replacePlaceholder(String placeholder, Player player);

}
