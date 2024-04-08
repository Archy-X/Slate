package dev.aurelium.slate.function;

import dev.aurelium.slate.info.ComponentPlaceholderInfo;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ComponentReplacer<T> {

    @Nullable
    String replace(ComponentPlaceholderInfo<T> info);

}
