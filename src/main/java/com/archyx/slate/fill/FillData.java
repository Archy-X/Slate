package com.archyx.slate.fill;

import fr.minuskube.inv.content.SlotPos;
import org.jetbrains.annotations.Nullable;

public class FillData {

    private final FillItem item;
    private final SlotPos[] slots;
    private final boolean enabled;

    public FillData(FillItem item, SlotPos[] slots, boolean enabled) {
        this.item = item;
        this.slots = slots;
        this.enabled = enabled;
    }

    public FillItem getItem() {
        return item;
    }

    @Nullable
    public SlotPos[] getSlots() {
        return slots;
    }

    public boolean isEnabled() {
        return enabled;
    }

}
