package com.archyx.slate.lore.type;

import com.archyx.slate.lore.LoreLine;
import com.archyx.slate.lore.LoreType;

public class ComponentLore extends LoreLine {

    private final String component;

    public ComponentLore(String component) {
        super(LoreType.COMPONENT);
        this.component = component;
    }

    public String getComponent() {
        return component;
    }
}
