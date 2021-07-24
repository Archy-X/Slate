package com.archyx.slate.menu;

import com.archyx.slate.item.active.ActiveItem;
import com.archyx.slate.item.active.ActiveSingleItem;
import com.archyx.slate.item.active.ActiveTemplateItem;
import org.bukkit.Bukkit;

public class ActiveMenu {

    private final MenuInventory menuInventory;

    public ActiveMenu(MenuInventory menuInventory) {
        this.menuInventory = menuInventory;
    }

    public ActiveSingleItem getItem(String itemName) {
        ActiveItem item = menuInventory.getActiveItem(itemName);
        if (item instanceof ActiveSingleItem) {
            return (ActiveSingleItem) item;
        }
        throw new IllegalArgumentException("Item with name " + itemName + " not found in menu " + menuInventory.getMenu().getName());
    }

    @SuppressWarnings("unchecked")
    public <C> ActiveTemplateItem<C> getItem(String itemName, Class<C> contextClass) {
        ActiveItem item = menuInventory.getActiveItem(itemName);
        if (item instanceof ActiveTemplateItem) {
            try {
                return (ActiveTemplateItem<C>) item;
            } catch (ClassCastException e) {
                Bukkit.getLogger().warning("Item template " + itemName + " does not have context of type " + contextClass.getName());
                e.printStackTrace();
            }
        }
        throw new IllegalArgumentException("Item with name " + itemName + " not found in menu " + menuInventory.getMenu().getName());
    }

}
