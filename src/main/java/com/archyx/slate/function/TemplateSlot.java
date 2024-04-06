package com.archyx.slate.function;

import com.archyx.slate.info.TemplateInfo;
import fr.minuskube.inv.content.SlotPos;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface TemplateSlot<T> {

    @Nullable
    SlotPos get(TemplateInfo<T> info);

}
