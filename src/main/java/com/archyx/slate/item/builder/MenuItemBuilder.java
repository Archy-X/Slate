package com.archyx.slate.item.builder;

import com.archyx.slate.Slate;
import com.archyx.slate.item.MenuItem;

import java.util.List;

public abstract class MenuItemBuilder {

    protected final Slate slate;
    protected String name;
    protected String displayName;
    protected List<String> lore;

    public MenuItemBuilder(Slate slate) {
        this.slate = slate;
    }

    public abstract MenuItem build();

    public MenuItemBuilder name(String name) {
        this.name = name;
        return this;
    }

    public MenuItemBuilder displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public MenuItemBuilder lore(List<String> lore) {
        this.lore = lore;
        return this;
    }

}
