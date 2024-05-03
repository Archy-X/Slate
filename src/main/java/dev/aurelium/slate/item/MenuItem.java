package dev.aurelium.slate.item;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.Action;
import dev.aurelium.slate.action.trigger.ClickTrigger;
import dev.aurelium.slate.lore.LoreLine;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public abstract class MenuItem {

    protected final Slate slate;
    private final String name;
    private final String displayName;
    private final List<LoreLine> lore;
    private final Map<ClickTrigger, List<Action>> actions;
    private final Map<String, Object> options;

    public MenuItem(Slate slate, String name, String displayName, List<LoreLine> lore, Map<ClickTrigger, List<Action>> actions, Map<String, Object> options) {
        this.slate = slate;
        this.name = name;
        this.displayName = displayName;
        this.lore = lore;
        this.actions = actions;
        this.options = options;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public String getDisplayName() {
        return displayName;
    }

    @Nullable
    public List<LoreLine> getLore() {
        return lore;
    }

    public Map<ClickTrigger, List<Action>> getActions() {
        return actions;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

}
