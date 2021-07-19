package com.archyx.slate.item;

import com.archyx.slate.Slate;
import com.archyx.slate.item.provider.SingleItemProvider;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SingleItem extends MenuItem {

    private final SlotPos position;
    private final ItemStack baseItem;
    private final SingleItemProvider provider;

    public SingleItem(Slate slate, String name, ItemStack baseItem, String displayName, List<String> lore, SlotPos position, SingleItemProvider provider) {
        super(slate, name, displayName, lore);
        this.position = position;
        this.baseItem = baseItem;
        this.provider = provider;
    }

    public SlotPos getPosition() {
        return position;
    }

    public ItemStack getBaseItem() {
        return baseItem.clone();
    }

    @Nullable
    public SingleItemProvider getProvider() {
        return provider;
    }

}
