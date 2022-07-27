package com.archyx.slate.item.provider;

@FunctionalInterface
public interface SingleItemConstructor<T> {
    T construct();
}