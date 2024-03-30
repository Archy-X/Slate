package com.archyx.slate.function;

import com.archyx.slate.info.PlaceholderInfo;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ItemReplacer {

    @Nullable
    String replace(PlaceholderInfo info);

}
