package com.archyx.slate.context;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ContextManager {

    private final Map<Class<?>, ContextProvider<?>> contexts;

    public ContextManager() {
        this.contexts = new HashMap<>();
    }

    public <C> void registerContext(Class<C> contextClass, ContextProvider<C> provider) {
        contexts.put(contextClass, provider);
    }

    @Nullable
    public ContextProvider<?> getContextProvider(Class<?> contextClass) {
        return contexts.get(contextClass);
    }

}
