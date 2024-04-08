package dev.aurelium.slate.info;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TemplateInfo<T> extends ItemInfo {

    private final T value;

    public TemplateInfo(Slate slate, Player player, ActiveMenu menu, ItemStack item, T value) {
        super(slate, player, menu, item);
        this.value = value;
    }

    public T value() {
        return value;
    }
}
