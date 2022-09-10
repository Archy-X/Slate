package com.archyx.slate.item;

import com.archyx.slate.Slate;
import com.archyx.slate.action.Action;
import com.archyx.slate.action.click.ClickAction;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class SingleItem extends MenuItem {

    private final SlotPos position;
    private final ItemStack baseItem;

    public SingleItem(Slate slate, String name, ItemStack baseItem, String displayName, List<String> lore, Map<ClickAction, List<Action>> actions, SlotPos position, Map<String, Object> options) {
        super(slate, name, displayName, lore, actions, options);
        this.position = position;
        this.baseItem = baseItem;
    }

    public SlotPos getPosition() {
        return position;
    }

    public ItemStack getBaseItem() {
        return baseItem.clone();
    }

}
