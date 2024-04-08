package dev.aurelium.slate.fill;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.item.MenuItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class FillItem extends MenuItem {

    private final ItemStack baseItem;

    public FillItem(Slate slate, ItemStack baseItem) {
        super(slate, "fill", " ", null, new LinkedHashMap<>(), new HashMap<>());
        this.baseItem = baseItem;
    }

    public ItemStack getBaseItem() {
        return baseItem;
    }

    public static FillItem getDefault(Slate slate) {
        return new FillItem(slate, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
    }

}
