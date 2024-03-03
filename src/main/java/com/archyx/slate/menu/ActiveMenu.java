package com.archyx.slate.menu;

import com.archyx.slate.component.MenuComponent;
import com.archyx.slate.context.ContextGroup;
import com.archyx.slate.item.MenuItem;
import com.archyx.slate.item.TemplateItem;
import com.archyx.slate.item.active.ActiveItem;
import com.archyx.slate.position.PositionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
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

    public Object getProperty(String name, Object def) {
        Object value = menuInventory.getProperties().get(name);
        if (value != null) {
            return value;
        } else {
            return def;
        }
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

    public Map<String, MenuComponent> getComponents() {
        return menuInventory.getMenu().getComponents();
    }

    public Map<String, String> getFormats() {
        return menuInventory.getMenu().getFormats();
    }

    /**
     * Gets a format from the menu's formats, returns the key if the format does not exist
     *
     * @param key The key of the format
     * @return The format, or the key if the format does not exist
     */
    @NotNull
    public String getFormat(String key) {
        return menuInventory.getMenu().getFormats().getOrDefault(key, key);
    }

    @SuppressWarnings("unchecked")
    public <T> void setPositionProvider(String templateName, T context, PositionProvider provider) {
        MenuItem menuItem = menuInventory.getMenu().getItems().get(templateName);
        if (menuItem instanceof TemplateItem) {
            TemplateItem<T> templateItem = (TemplateItem<T>) menuItem;
            templateItem.getPositionsMap().put(context, provider);
        }
    }

    public Map<String, ContextGroup> getContextGroups(String templateName) {
        MenuItem menuItem = menuInventory.getMenu().getItems().get(templateName);
        if (menuItem instanceof TemplateItem) {
            TemplateItem<?> templateItem = (TemplateItem<?>) menuItem;
            return templateItem.getContextGroups();
        }
        return new HashMap<>();
    }

}
