package com.archyx.slate.lore.type;

import com.archyx.slate.lore.LoreLine;
import com.archyx.slate.lore.LoreType;

public class ComponentLore extends LoreLine {

    private final String[] components;

    public ComponentLore(String[] components) {
        super(LoreType.COMPONENT);
        this.components = components;
    }

    public String[] getComponents() {
        return components;
    }
}
