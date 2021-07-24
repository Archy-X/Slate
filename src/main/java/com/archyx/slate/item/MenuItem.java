package com.archyx.slate.item;

import com.archyx.slate.Slate;
import com.archyx.slate.action.Action;
import com.archyx.slate.action.click.ClickAction;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public abstract class MenuItem {

    protected final Slate slate;
    private final String name;
    private final String displayName;
    private final List<String> lore;
    private final Map<ClickAction, List<Action>> actions;

    public MenuItem(Slate slate, String name, String displayName, List<String> lore, Map<ClickAction, List<Action>> actions) {
        this.slate = slate;
        this.name = name;
        this.displayName = displayName;
        this.lore = lore;
        this.actions = actions;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public String getDisplayName() {
        return displayName;
    }

    @Nullable
    public List<String> getLore() {
        return lore;
    }

    public Map<ClickAction, List<Action>> getActions() {
        return actions;
    }

}
