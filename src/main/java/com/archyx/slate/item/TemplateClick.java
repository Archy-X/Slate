package com.archyx.slate.item;

import com.archyx.slate.menu.ActiveMenu;
import fr.minuskube.inv.content.SlotPos;
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
