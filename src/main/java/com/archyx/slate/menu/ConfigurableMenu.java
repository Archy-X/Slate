package com.archyx.slate.menu;

import com.archyx.slate.component.MenuComponent;
import com.archyx.slate.fill.FillData;
import com.archyx.slate.item.MenuItem;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ConfigurableMenu {

    private final String name;
    private final String title;
    private final int size;
    private final Map<String, MenuItem> items;
    private final Map<String, MenuComponent> components;
    private final MenuProvider provider;
    private final FillData fillData;
    private final Map<String, Object> options;

    public ConfigurableMenu(String name, String title, int size, Map<String, MenuItem> items, Map<String, MenuComponent> components, MenuProvider provider, FillData fillData, Map<String, Object> options) {
        this.name = name;
        this.title = title;
        this.size = size;
        this.items = items;
        this.components = components;
        this.provider = provider;
        this.fillData = fillData;
        this.options = options;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public int getSize() {
        return size;
    }

    public Map<String, MenuItem> getItems() {
        return items;
    }

    public Map<String, MenuComponent> getComponents() {
        return components;
    }

    @Nullable
    public MenuProvider getProvider() {
        return provider;
    }

    public FillData getFillData() {
        return fillData;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

}
