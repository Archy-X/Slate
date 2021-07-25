package com.archyx.slate.fill;

import com.archyx.slate.Slate;
import com.archyx.slate.item.MenuItem;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;

public class FillItem extends MenuItem {

    private final ItemStack baseItem;

    public FillItem(Slate slate, ItemStack baseItem) {
        super(slate, "fill", " ", null, new LinkedHashMap<>());
        this.baseItem = baseItem;
    }

    public ItemStack getBaseItem() {
        return baseItem;
    }

    public static FillItem getDefault(Slate slate) {
        return new FillItem(slate, XMaterial.BLACK_STAINED_GLASS_PANE.parseItem());
    }

}
