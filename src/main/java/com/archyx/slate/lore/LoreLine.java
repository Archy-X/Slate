package com.archyx.slate.lore;

public abstract class LoreLine {

    private final LoreType type;

    public LoreLine(LoreType type) {
        this.type = type;
    }

    public LoreType getType() {
        return type;
    }
}
