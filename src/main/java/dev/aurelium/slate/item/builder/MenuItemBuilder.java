package dev.aurelium.slate.item.builder;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.Action;
import dev.aurelium.slate.action.click.ClickAction;
import dev.aurelium.slate.item.MenuItem;
import dev.aurelium.slate.lore.LoreLine;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class MenuItemBuilder {

    protected final Slate slate;
    protected String name;
    protected String displayName;
    protected List<LoreLine> lore;
    protected Map<ClickAction, List<Action>> actions;
    protected Map<String, Object> options;

    public MenuItemBuilder(Slate slate) {
        this.slate = slate;
        this.actions = new LinkedHashMap<>();
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

    public MenuItemBuilder lore(List<LoreLine> lore) {
        this.lore = lore;
        return this;
    }

    public MenuItemBuilder actions(Map<ClickAction, List<Action>> actions) {
        this.actions = actions;
        return this;
    }

    public MenuItemBuilder options(Map<String, Object> options) {
        this.options = options;
        return this;
    }

}
