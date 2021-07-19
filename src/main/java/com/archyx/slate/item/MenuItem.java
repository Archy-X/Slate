package com.archyx.slate.item;

import com.archyx.slate.Slate;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class MenuItem {

    protected final Slate slate;
    private final String name;
    private final String displayName;
    private final List<String> lore;

    public MenuItem(Slate slate, String name, String displayName, List<String> lore) {
        this.slate = slate;
        this.name = name;
        this.displayName = displayName;
        this.lore = lore;
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

}
