package dev.aurelium.slate.item;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.Action;
import dev.aurelium.slate.action.click.ClickAction;
import dev.aurelium.slate.lore.LoreLine;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class SingleItem extends MenuItem {

    private final SlotPos position;
    private final ItemStack baseItem;

    public SingleItem(Slate slate, String name, ItemStack baseItem, String displayName, List<LoreLine> lore, Map<ClickAction, List<Action>> actions, SlotPos position, Map<String, Object> options) {
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
