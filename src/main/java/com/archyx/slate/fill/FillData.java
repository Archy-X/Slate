package com.archyx.slate.fill;

public class FillData {

    private final FillItem item;
    private final boolean enabled;

    public FillData(FillItem item, boolean enabled) {
        this.item = item;
        this.enabled = enabled;
    }

    public FillItem getItem() {
        return item;
    }

    public boolean isEnabled() {
        return enabled;
    }

}
