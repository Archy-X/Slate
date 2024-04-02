package com.archyx.slate.info;

import com.archyx.slate.Slate;
import com.archyx.slate.component.ComponentData;
import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;

public class ComponentPlaceholderInfo<T> extends TemplatePlaceholderInfo<T> {

    private final ComponentData component;

    public ComponentPlaceholderInfo(Slate slate, Player player, String placeholder, ActiveMenu menu, PlaceholderData data, ComponentData component, T value) {
        super(slate, player, placeholder, menu, data, value);
        this.component = component;
    }

    public ComponentData component() {
        return component;
    }
}
