package com.archyx.slate.item.provider;

import com.archyx.slate.item.option.Option;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public interface ItemProvider {

    @NotNull
    default Set<Option<?>> getItemOptions() {
        return new HashSet<>();
    }

}
