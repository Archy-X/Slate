package com.archyx.slate.item.provider;

import org.bukkit.entity.Player;

import java.util.Set;

public interface TemplateItemProvider<C> {

    Class<C> getContext();

    String replacePlaceholder(String placeholder, Player player, C context);

    Set<C> getDefinedContexts();

}
