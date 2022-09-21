package com.archyx.slate.menu;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

@FunctionalInterface
public interface MenuConstructor<T> {

    @NotNull
    T construct(Map<String, Object> properties);

}
