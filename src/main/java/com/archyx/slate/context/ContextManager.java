package com.archyx.slate.context;

import com.google.common.primitives.*;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class ContextManager {

    private final Map<Class<?>, ContextProvider<?>> contexts;
    private final Map<String, ContextProvider<?>> keyedContexts;

    public ContextManager() {
        this.contexts = new HashMap<>();
        this.keyedContexts = new HashMap<>();
        registerDefaults();
    }

    /**
     * Registers a context provider to the given context type
     *
     * @param contextClass The class to add the context provider for
     * @param provider The context provider
     * @param <C> The context type
     */
    public <C> void registerContext(Class<C> contextClass, ContextProvider<C> provider) {
        contexts.put(contextClass, provider);
    }

    public <C> void registerContext(String key, Class<C> contextClass, ContextProvider<C> provider) {
        registerContext(contextClass, provider);
        keyedContexts.put(key, provider);
    }

    @Nullable
    public ContextProvider<?> getContextProvider(Class<?> contextClass) {
        return contexts.get(contextClass);
    }

    @Nullable
    public ContextProvider<?> getContextProvider(String key) {
        return keyedContexts.get(key);
    }

    private void registerDefaults() {
        registerContext(Integer.class, Ints::tryParse);
        registerContext(Long.class, Longs::tryParse);
        registerContext(Float.class, Floats::tryParse);
        registerContext(Double.class, Doubles::tryParse);
        registerContext(Short.class, input -> {
            Integer integer = Ints.tryParse(input);
            if (integer != null) {
                return (short) integer.intValue();
            }
            return null;
        });
        registerContext(Byte.class, input -> {
            Integer integer = Ints.tryParse(input);
            if (integer != null) {
                return (byte) integer.intValue();
            }
            return null;
        });
        registerContext(Boolean.class, input -> input.equalsIgnoreCase("true"));
        registerContext(Character.class, input -> {
            if (input.length() == 1) {
                return input.toCharArray()[0];
            }
            return null;
        });
        registerContext(String.class, input -> input);
    }

}
