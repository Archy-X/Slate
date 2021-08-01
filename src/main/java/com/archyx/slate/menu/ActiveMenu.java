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

    /**
     * Hides an item from showing up in the current menu, will not delete the item in configs
     *
     * @param itemName The name of the item to hide
     */
    public void hideItem(String itemName) {
        menuInventory.removeActiveItem(itemName);
    }

    /**
     * Gets the current page of the menu, 0 is the first page
     *
     * @return The current page number, 0 if there is no pagination set up
     */
    public int getCurrentPage() {
        return menuInventory.getCurrentPage();
    }

    /**
     * Gets the total number of pages in the menu
     *
     * @return The total number of pages, 1 if there is no pagination set up
     */
    public int getTotalPages() {
        return menuInventory.getTotalPages();
    }

    public Object getProperty(String name) {
        return menuInventory.getProperties().get(name);
    }

}
