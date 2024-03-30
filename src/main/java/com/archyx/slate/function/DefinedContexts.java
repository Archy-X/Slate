package com.archyx.slate.function;

import com.archyx.slate.info.MenuInfo;

import java.util.Set;

@FunctionalInterface
public interface DefinedContexts<T> {

    Set<T> get(MenuInfo info);

}
