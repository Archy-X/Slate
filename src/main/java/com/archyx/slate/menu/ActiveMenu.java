package com.archyx.slate.menu;

import com.archyx.slate.item.MenuItem;
import com.archyx.slate.item.active.ActiveItem;
import org.jetbrains.annotations.Nullable;

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

    public MenuProvider getMenuProvider() {
        return menuInventory.getMenuProvider();
    }

    public <T> T getMenuProvider(Class<T> menuProviderClass) {
        MenuProvider menuProvider = getMenuProvider();
        if (menuProviderClass.isInstance(menuProvider)) {
            return menuProviderClass.cast(menuProvider);
        } else {
            throw new IllegalArgumentException("MenuProvider object cannot be casted to class " + menuProviderClass.getName());
        }
    }

    public void reload() {
        menuInventory.init(menuInventory.getPlayer(), menuInventory.getContents());
    }

    public void setCooldown(String itemName, int cooldown) {
        ActiveItem activeItem = menuInventory.getActiveItem(itemName);
        if (activeItem != null) {
            activeItem.setCooldown(cooldown);
        }
    }

    @Nullable
    public Object getOption(String key) {
        return menuInventory.getMenu().getOptions().get(key);
    }

    @Nullable
    public <T> T getOption(Class<T> clazz, String key) {
        try {
            return clazz.cast(menuInventory.getMenu().getOptions().get(key));
        } catch (ClassCastException e) {
            return null;
        }
    }

    public Object getOption(String key, Object def) {
        Object obj = menuInventory.getMenu().getOptions().get(key);
        if (obj != null) {
            return obj;
        } else {
            return def;
        }
    }

    public <T> T getOption(Class<T> clazz, String key, T def) {
        try {
            T result = clazz.cast(menuInventory.getMenu().getOptions().get(key));
            if (result != null) {
                return result;
            } else {
                return def;
            }
        } catch (ClassCastException e) {
            return def;
        }
    }

    @Nullable
    public Object getItemOption(String itemName, String key) {
        MenuItem menuItem = menuInventory.getMenu().getItems().get(itemName);
        if (menuItem != null) {
            return menuItem.getOptions().get(key);
        }
        return null;
    }

    public Object getItemOption(String itemName, String key, Object def) {
        MenuItem menuItem = menuInventory.getMenu().getItems().get(itemName);
        if (menuItem != null) {
            Object obj = menuItem.getOptions().get(key);
            if (obj != null) {
                return obj;
            }
        }
        return def;
    }

}
