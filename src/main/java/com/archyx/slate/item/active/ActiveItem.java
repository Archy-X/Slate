package com.archyx.slate.item.active;

public abstract class ActiveItem {

    private boolean hidden;

    public ActiveItem() {
        this.hidden = false;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

}
