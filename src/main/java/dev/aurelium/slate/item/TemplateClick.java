package dev.aurelium.slate.item;

import dev.aurelium.slate.inv.content.SlotPos;
import dev.aurelium.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class TemplateClick<T> extends ItemClick {

    private final T value;

    public TemplateClick(Player player, InventoryClickEvent event, ItemStack item, SlotPos pos, ActiveMenu menu, T value) {
        super(player, event, item, pos, menu);
        this.value = value;
    }

    public T value() {
        return value;
    }
}
