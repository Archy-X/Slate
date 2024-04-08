package dev.aurelium.slate.function;

import dev.aurelium.slate.info.PlaceholderInfo;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ItemReplacer {

    @Nullable
    String replace(PlaceholderInfo info);

}
