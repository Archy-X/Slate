package com.archyx.slate.item.provider;

@FunctionalInterface
public interface TemplateItemConstructor<T> {
    T construct();
}