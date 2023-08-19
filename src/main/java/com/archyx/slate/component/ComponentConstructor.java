package com.archyx.slate.component;

@FunctionalInterface
public interface ComponentConstructor<T> {

    T construct();

}
