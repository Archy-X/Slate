package com.archyx.slate.menu;

import com.archyx.slate.fill.FillData;
import com.archyx.slate.item.MenuItem;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ConfigurableMenu {

    private final String name;
    private final String title;
    private final int size;
    private final Map<String, MenuItem> items;
    private final MenuProvider provider;
    private final FillData fillData;
    private final Map<String, Object> options;

    public ConfigurableMenu(String name, String title, int size, Map<String, MenuItem> items, MenuProvider provider, FillData fillData, Map<String, Object> options) {
        this.name = name;
        this.title = title;
        this.size = size;
        this.items = items;
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
