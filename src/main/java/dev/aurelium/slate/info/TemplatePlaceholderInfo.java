package dev.aurelium.slate.info;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.item.provider.PlaceholderData;
import dev.aurelium.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;

public class TemplatePlaceholderInfo<T> extends PlaceholderInfo {

    private final T value;

    public TemplatePlaceholderInfo(Slate slate, Player player, String placeholder, ActiveMenu menu, PlaceholderData data, T value) {
        super(slate, player, placeholder, menu, data);
        this.value = value;
    }

    public T value() {
        return value;
    }
}
