package dev.aurelium.slate.function;

import dev.aurelium.slate.info.MenuInfo;

import java.util.Set;

@FunctionalInterface
public interface DefinedContexts<T> {

    Set<T> get(MenuInfo info);

}
