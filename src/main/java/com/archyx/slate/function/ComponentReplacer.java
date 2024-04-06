package com.archyx.slate.function;

import com.archyx.slate.info.ComponentPlaceholderInfo;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ComponentReplacer<T> {

    @Nullable
    String replace(ComponentPlaceholderInfo<T> info);

}
