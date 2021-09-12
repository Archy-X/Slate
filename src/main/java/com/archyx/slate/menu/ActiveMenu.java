package com.archyx.slate.menu;

import com.archyx.slate.item.active.ActiveItem;

import java.util.Map;

public class ActiveMenu {

    private final MenuInventory menuInventory;

    public ActiveMenu(MenuInventory menuInventory) {
        this.menuInventory = menuInventory;
    }

    public String getName() {
        return menuInventory.getMenu().getName();
    }

    /**
     * Hides or shows an item in the current menu, will not delete the item in configs.
     * Will not take effect unless the menu is reloaded.
     *
     * @param itemName The name of the item to hide
     */
    public void setHidden(String itemName, boolean hidden) {
        ActiveItem activeItem = menuInventory.getActiveItem(itemName);
        if (activeItem != null) {
            activeItem.setHidden(hidden);
        }
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

    public Map<String, Object> getProperties() {
        return menuInventory.getProperties();
    }

    public void setProperty(String name, Object value) {
        menuInventory.getProperties().put(name, value);
    }

    public void reload() {
        menuInventory.init(menuInventory.getPlayer(), menuInventory.getContents());
    }

}
