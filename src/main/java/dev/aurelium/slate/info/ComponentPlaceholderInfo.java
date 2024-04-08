package dev.aurelium.slate.info;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.component.ComponentData;
import dev.aurelium.slate.item.provider.PlaceholderData;
import dev.aurelium.slate.menu.ActiveMenu;
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
