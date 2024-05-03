package dev.aurelium.slate.item;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.Action;
import dev.aurelium.slate.action.trigger.ClickTrigger;
import dev.aurelium.slate.inv.content.SlotPos;
import dev.aurelium.slate.lore.LoreLine;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class SingleItem extends MenuItem {

    private final List<SlotPos> positions;
    private final ItemStack baseItem;

    public SingleItem(Slate slate, String name, ItemStack baseItem, String displayName, List<LoreLine> lore, Map<ClickTrigger, List<Action>> actions, List<SlotPos> positions, Map<String, Object> options) {
        super(slate, name, displayName, lore, actions, options);
        this.positions = positions;
        this.baseItem = baseItem;
    }

    public List<SlotPos> getPositions() {
        return positions;
    }

    public ItemStack getBaseItem() {
        return baseItem.clone();
    }

}
