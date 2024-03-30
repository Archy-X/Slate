package com.archyx.slate.item;

import com.archyx.slate.menu.ActiveMenu;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ItemClick {

    private final Player player;
    private final InventoryClickEvent event;
    private final ItemStack item;
    private final SlotPos pos;
    private final ActiveMenu menu;

    public ItemClick(Player player, InventoryClickEvent event, ItemStack item, SlotPos pos, ActiveMenu menu) {
        this.player = player;
        this.event = event;
        this.item = item;
        this.pos = pos;
        this.menu = menu;
    }

    public Player player() {
        return player;
    }

    public InventoryClickEvent event() {
        return event;
    }

    public ItemStack item() {
        return item;
    }

    public SlotPos pos() {
        return pos;
    }

    public ActiveMenu menu() {
        return menu;
    }
}
