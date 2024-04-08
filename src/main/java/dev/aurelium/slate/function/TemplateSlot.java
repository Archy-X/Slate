package dev.aurelium.slate.function;

import dev.aurelium.slate.info.TemplateInfo;
import dev.aurelium.slate.inv.content.SlotPos;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface TemplateSlot<T> {

    @Nullable
    SlotPos get(TemplateInfo<T> info);

}
