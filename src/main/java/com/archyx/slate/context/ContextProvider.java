package com.archyx.slate.context;

import org.jetbrains.annotations.Nullable;

public interface ContextProvider<C> {

    @Nullable
    C parse(String input);

}
