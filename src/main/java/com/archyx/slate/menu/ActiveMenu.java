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

    /**
     * Gets an active single item in the menu used to add behavior to
     *
     * @param itemName The item name, must be a single item, not a template
     * @return The active single item
     * @throws IllegalArgumentException If an item with the specified name does not exist in the menu
     */
    public ActiveSingleItem getItem(String itemName) {
        ActiveItem item = menuInventory.getActiveItem(itemName);
        if (item instanceof ActiveSingleItem) {
            return (ActiveSingleItem) item;
        }
        throw new IllegalArgumentException("Item with name " + itemName + " not found in menu " + menuInventory.getMenu().getName());
    }

    /**
     * Gets an active template item in the menu used to add behavior to
     *
     * @param itemName The item name, must be a template, not a single item
     * @return The active template item
     * @throws IllegalArgumentException If an item with the specified name does not exist in the menu
     */
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
